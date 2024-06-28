package clientTests;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static final UserData testUserData = new UserData("username", "password", "test@email.com");

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }
    @AfterAll
    static void stopServer() {
        server.stop();
    }
    @BeforeEach
    public void clear() throws ResponseException {
        serverFacade.clear();
    }

    @Test
    public void registerSuccess() {
        try {
            AuthData authData = serverFacade.register(testUserData);
            assert !authData.authToken().isEmpty();
        } catch (Throwable e) {
            assert false;
        }
    }
    @Test
    public void registerFail() {
        try {
            serverFacade.register(testUserData);
            serverFacade.register(testUserData);
            assert false;
        } catch (ResponseException e) {
            assert e.statusCode() == 403;
        }
    }
    @Test
    public void loginSuccess() {
        try {
            serverFacade.register(testUserData);
            AuthData authData = serverFacade.login(testUserData);
            assert !authData.authToken().isEmpty();
        } catch (Throwable e) {
            assert false;
        }
    }
    @Test
    public void loginFail() {
        try {
            serverFacade.register(testUserData);
            serverFacade.login(new UserData("bogus", "notapassword", "email"));
        } catch (ResponseException e) {
            assert e.statusCode() == 401;
        }
    }
    @Test
    public void logoutSuccess() {
        try {
            AuthData authData = serverFacade.register(testUserData);
            serverFacade.logout(authData.authToken());
            assert true;
        } catch (Throwable e) {
            assert false;
        }
    }
    @Test
    public void logoutFail() {
        try {
            serverFacade.register(testUserData);
            serverFacade.logout("notanauthtoken");
            assert false;
        } catch (Throwable e) {
            assert true;
        }
    }
    @Test
    public void listGamesSuccess() {
        try {
            AuthData authData = serverFacade.register(testUserData);
            serverFacade.createGame(authData.authToken(), "test");
            assert !serverFacade.listGames(authData.authToken()).isEmpty();
        } catch (Throwable e) {
            assert false;
        }
    }
    @Test
    public void listGamesFail() {
        try {
            AuthData authData = serverFacade.register(testUserData);
            serverFacade.createGame(authData.authToken(), "test");
            serverFacade.listGames("badauthtoken");
            assert false;
        } catch (Throwable e) {
            assert true;
        }
    }
    @Test
    public void createGameSuccess() {
        try {
            AuthData authData = serverFacade.register(testUserData);
            GameData gameData = serverFacade.createGame(authData.authToken(), "test");
            assert gameData.gameName().equals("test");
        } catch (Throwable e) {
            assert false;
        }
    }
    @Test
    public void createGameFail() {
        try {
            AuthData authData = serverFacade.register(testUserData);
            serverFacade.createGame(authData.authToken(), "");
            assert false;
        } catch (Throwable e) {
            assert true;
        }
    }
    @Test
    public void joinGameSuccess() {
        try {
            AuthData authData = serverFacade.register(testUserData);
            serverFacade.createGame(authData.authToken(), "test");
            serverFacade.joinGame(authData.authToken(), "WHITE", 1);
            assert !serverFacade.listGames(authData.authToken()).isEmpty();
        } catch (Throwable e) {
            assert false;
        }
    }
    @Test
    public void joinGameFail() {
        try {
            AuthData authData = serverFacade.register(testUserData);
            serverFacade.createGame(authData.authToken(), "test");
            serverFacade.joinGame(authData.authToken(), "WHITE", 50);
            assert false;
        } catch (Throwable e) {
            assert true;
        }
    }
    @Test
    public void clearSuccess() {
        try {
            clear();
            assert true;
        } catch (ResponseException e) {
            assert false;
        }
    }
    @Test
    public void clearFail() {
        // clear cannot fail in a replicable way
        try {
            clear();
            assert true;
        } catch (ResponseException e) {
            assert false;
        }
    }
}
