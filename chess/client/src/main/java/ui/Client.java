package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;
import webSocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Client {
    private final Repl repl;  // This IS the ServerMessageHandler/NotificationHandler
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private ClientState clientState = ClientState.SIGNED_OUT;
    private String authToken = null;
    private HashMap<Integer, Integer> gameListMapping;
    private ChessGame.TeamColor activeColor = null;
    private Integer activeGameID = null;
    protected ChessGame currentGame;

    final HashMap<String, Integer> columnToCoordinate = new HashMap<>() {{
        put("a", 8);
        put("b", 7);
        put("c", 6);
        put("d", 5);
        put("e", 4);
        put("f", 3);
        put("g", 2);
        put("h", 1);
    }};

    private enum ClientState {
        SIGNED_IN, SIGNED_OUT, IN_GAME
    }

    public Client(String serverUrl, Repl repl) throws ResponseException {
        server = new ServerFacade(serverUrl);
        this.repl = repl;
        ws = new WebSocketFacade(serverUrl, repl);
    }

    public String eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        switch (this.clientState) {
            case SIGNED_OUT -> {
                return switch (cmd) {
                    case "login" -> login();
                    case "register" -> register();
                    case "quit" -> "";
                    default -> help();
                };
            }
            case SIGNED_IN -> {
                gameListMapping = mapGames();
                return switch (cmd) {
                    case "logout" -> logout();
                    case "creategame" -> createGame(params);
                    case "listgames" -> listGames();
                    case "joingame" -> joinGame(params);
                    case "joinobserver" -> joinObserver(params);
                    case "quit" -> "";
                    default -> help();
                };
            }
            case IN_GAME -> {
                return switch (cmd) {
                    case "redrawboard" -> redrawBoard();
                    case "leave" -> leaveGame();
                    case "makemove" -> makeMove();
                    case "resign" -> resign();
                    case "highlightmoves" -> highlightMoves();
                    default -> help();
                };
            }
            default -> throw new RuntimeException("Bad client state.");
        }
    }

    private String register() throws ResponseException {
        String username = repl.prompt("Username: ");
        String password = repl.prompt("Password: ");
        String email = repl.prompt("Email: ");
        AuthData authData = server.register(new UserData(username, password, email));
        authToken = authData.authToken();
        clientState = ClientState.SIGNED_IN;
        return "User registered.\n";
    }
    private String login() throws ResponseException {
        String username = repl.prompt("Username: ");
        String password = repl.prompt("Password: ");
        AuthData authData = server.login(new UserData(username, password, null));
        authToken = authData.authToken();
        clientState = ClientState.SIGNED_IN;
        return "Logged in.\n";
    }
    private String logout() throws ResponseException {
        server.logout(authToken);
        clientState = ClientState.SIGNED_OUT;
        return "Logged out.\n";
    }
    private String createGame(String[] params) throws ResponseException {
        if (params.length != 1) {
            return help();
        }
        String gameName = params[0];
        server.createGame(authToken, gameName);
        return "Game created.\n";
    }
    private String listGames() throws ResponseException {
        ArrayList<GameData> games = server.listGames(authToken);
        if (games.isEmpty()) {
            return "No games found.\n";
        }
        StringBuilder gameListString = new StringBuilder();
        for (GameData game : games) {
            gameListString.append(gameListMapping.get(game.gameID())).append(". ");
            gameListString.append("Game Name: %s, ".formatted(game.gameName()));
            gameListString.append("White Player: %s, ".formatted(game.whiteUsername()));
            gameListString.append("Black Player: %s".formatted(game.blackUsername()));
            gameListString.append("\n");
        }
        return gameListString.toString();
    }
    private String joinGame(String[] params) throws ResponseException {
        if (params.length != 2) {
            return help();
        }
        String playerColor = params[0];
        String gameNumber = params[1];
        activeGameID = getGameID(gameNumber);
        server.joinGame(authToken, playerColor, activeGameID);

        ChessGame.TeamColor teamColor = null;
        if (Objects.equals(playerColor, "black")) {
            teamColor = ChessGame.TeamColor.BLACK;
        } else if (Objects.equals(playerColor, "white")) {
            teamColor = ChessGame.TeamColor.WHITE;
        }
        ws.joinPlayer(authToken, getGameID(gameNumber), teamColor);
        activeColor = teamColor;
        clientState = ClientState.IN_GAME;
        return "Successful join as player.\n";
    }
    private String joinObserver(String[] params) throws ResponseException {
        if (params.length != 1) {
            return help();
        }
        String gameNumber = params[0];
        activeGameID = getGameID(gameNumber);
        server.joinGame(authToken, null, activeGameID);
        ws.joinObserver(authToken, getGameID(gameNumber));
        activeColor = null;
        clientState = ClientState.IN_GAME;
        return "Successful join as observer.\n";
    }
    public String leaveGame() throws ResponseException {
        if (activeColor != null) {  // this block uses HTTP to remove the username from the game database
            String color = null;
            if (activeColor == ChessGame.TeamColor.BLACK) {
                color = "black";
            } else if (activeColor == ChessGame.TeamColor.WHITE) {
                color = "white";
            }
            server.joinGame(null, color, activeGameID);
            activeColor = null;
        }
        ws.leave(authToken, activeGameID);
        clientState = ClientState.SIGNED_IN;
        activeGameID = null;
        return "Left game.\n";
    }
    private String redrawBoard() {
        printBoard();
        return "Board redrawn.";
    }
    public String resign() throws ResponseException {
        ws.resign(authToken, activeGameID);
        return "Resigned game.\n";
    }
    private String makeMove() throws ResponseException {
        int startCol = columnToCoordinate.get(repl.prompt("Piece location column: "));
        int startRow = Integer.parseInt(repl.prompt("Piece location row: "));
        int endCol = columnToCoordinate.get(repl.prompt("Move to column: "));
        int endRow = Integer.parseInt(repl.prompt("Move to row: "));
        ChessPosition start = new ChessPosition(startRow, startCol);
        ChessPosition end = new ChessPosition(endRow, endCol);
        ChessPiece.PieceType promotionPiece = null;
        switch (repl.prompt("Promotion piece (return for null): ")) {
            case "queen" -> {promotionPiece = ChessPiece.PieceType.QUEEN;}
            case "rook" -> {promotionPiece = ChessPiece.PieceType.ROOK;}
            case "bishop" -> {promotionPiece = ChessPiece.PieceType.BISHOP;}
            case "knight" -> {promotionPiece = ChessPiece.PieceType.KNIGHT;}
        }

        ws.makeMove(authToken, activeGameID, new ChessMove(start, end, promotionPiece));
        return "Sent move to server.\n";
    }
    private String highlightMoves() throws ResponseException {
        int col = columnToCoordinate.get(repl.prompt("Piece location column: "));
        int row = Integer.parseInt(repl.prompt("Piece location row: "));
        ChessPosition position = new ChessPosition(row, col);
        if (activeColor == TeamColor.WHITE) {
            return new BoardArtist(currentGame.getBoard()).showMovesReverse(currentGame.validMoves(position));
        } else {
            return new BoardArtist(currentGame.getBoard()).showMoves(currentGame.validMoves(position));
        }
    }
    public String help() {
        return switch (clientState) {
            case SIGNED_OUT -> """
                    - help (see help text)
                    - login (create new session)
                    - register (create new user)
                    - quit (close the client)
                    """;

            case SIGNED_IN -> """
                    - help (see help text)
                    - logout (end session)
                    - createGame <gameName> (create a new game)
                    - listGames (list all games on the server)
                    - joinGame [white|black] <gameNumber> (join an existing game as a player)
                    - joinObserver <gameNumber> (join an existing game as an observer)
                    - quit (close the client)
                    """;

            case IN_GAME -> """
                    - help (see help text)
                    - redrawBoard (redraw the chess board)
                    - leave (leave the game)
                    - makeMove (make a move)
                    - resign (resign the game)
                    - highlightMoves (see all legal moves for a piece)
                    """;
        };
    }
    protected void printBoard() {
        BoardArtist boardArtist = new BoardArtist(currentGame.getBoard());
        String board = switch (activeColor) {
            case WHITE -> boardArtist.drawReverseBoard();
            case BLACK -> boardArtist.drawBoard();
            case null -> boardArtist.drawReverseBoard() + "\n" + boardArtist.drawBoard();
        };
        System.out.println(board);
    }
    private HashMap<Integer, Integer> mapGames() throws ResponseException {
        HashMap<Integer, Integer> mapping = new HashMap<>();
        ArrayList<GameData> games = server.listGames(authToken);
        int num = 1;
        for (GameData game : games) {
            mapping.put(num++, game.gameID());
        }
        return mapping;
    }
    private Integer getGameID(String gameNumber) {
        Integer gameID = -1;
        if (gameListMapping.get(Integer.parseInt(gameNumber)) != null) {
            gameID = gameListMapping.get(Integer.parseInt(gameNumber));
        }
        return gameID;
    }
}
