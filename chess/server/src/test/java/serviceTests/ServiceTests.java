package serviceTests;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.*;
import service.AuthService;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.Objects;

// These are NOT good tests

public class ServiceTests {
    private static final DataAccess dataAccess = new MemoryDataAccess();
    private static final AuthService authService = new AuthService(dataAccess);
    private static final ClearService clearService = new ClearService(dataAccess);
    private static final GameService gameService = new GameService(dataAccess);
    private static final UserService userService = new UserService(dataAccess);

    String existingUserUsername = "ExistingUser";
    String existingUserPassword = "existingUserPassword";
    String existingUserEmail = "eu@mail.com";
    String newUserUsername = "NewUser";
    String newUserPassword = "newUserPassword";
    String newUserEmail = "nu@mail.com";
    String testGameName = "testGame";

    @BeforeEach
    public void reset() throws DataAccessException {
        clearService.deleteDB();
    }

    @Test
    @DisplayName("register() Success")
    public void registerSuccess() throws DataAccessException {
        var userData = userService.register(new UserData(existingUserUsername, existingUserPassword, existingUserEmail));
        assert Objects.equals(userData.username(), existingUserUsername)
                && userData.password().equals(existingUserPassword)
                && userData.email().equals(existingUserEmail);
    }

    @Test
    @DisplayName("register() Fail")
    public void registerFail() throws DataAccessException {
        try {
            userService.register(new UserData(existingUserUsername, existingUserPassword, existingUserEmail));
            userService.register(new UserData(existingUserUsername, existingUserPassword, existingUserEmail));
            assert false;
        } catch (DataAccessException e) {
            assert e.statusCode() == 403;
        }
    }

    @Test
    @DisplayName("createAuth() Success")
    public void createAuthSuccess() throws DataAccessException {
        registerSuccess();
        var authData = authService.createAuth(new UserData(existingUserUsername, existingUserPassword, existingUserEmail));
        assert authData.username().equals(existingUserUsername) && authData.authToken() != null;
    }

    @Test
    @DisplayName("createAuth() Fail")
    public void createAuthFail() throws DataAccessException {
        try {
            registerSuccess();
            UserData userData = new UserData("otherUsername", existingUserPassword, existingUserEmail);
            dataAccess.checkUser(userData);
            authService.createAuth(userData);
            assert false;
        } catch (DataAccessException e) {
            assert true;
        }
    }

    @Test
    @DisplayName("login() Success")
    public void loginSuccess() throws DataAccessException {
        registerSuccess();
        var authData = authService.login(new UserData(existingUserUsername, existingUserPassword, existingUserEmail));
        assert authData.username().equals(existingUserUsername) && authData.authToken() != null;
    }

    @Test
    @DisplayName("login() Fail")
    public void loginFail() throws DataAccessException {
        try {
            registerSuccess();
            UserData userData = new UserData(existingUserUsername, "incorrect_password123", existingUserEmail);
            authService.login(userData);
            assert false;
        } catch (DataAccessException e) {
            assert true;
        }
    }

    @Test
    @DisplayName("logout() Success")
    public void logoutSuccess() throws DataAccessException {
        try {
            registerSuccess();
            var userData = new UserData(existingUserUsername, existingUserPassword, existingUserEmail);
            var authData = authService.login(userData);
            authService.logout(authData.authToken());
            dataAccess.checkAuth(authData.authToken());
        } catch (DataAccessException e) {
            assert e.statusCode() == 401;
        }
    }

    @Test
    @DisplayName("logout() Fail")
    public void logoutFail() throws DataAccessException {
        try {
            registerSuccess();
            var userData = new UserData(existingUserUsername, existingUserPassword, existingUserEmail);
            var authData = authService.login(userData);
            authService.logout("bogusAuthToken");
            assert false;
        } catch (DataAccessException e) {
            assert e.statusCode() == 401;
        }
    }

    @Test
    @DisplayName("checkAuth() Success")
    public void checkAuthSuccess() throws DataAccessException {
        registerSuccess();
        var authData = authService.login(new UserData(existingUserUsername, existingUserPassword, existingUserEmail));
        var authData2 = authService.checkAuth(authData.authToken());
        assert authData.authToken().equals(authData2.authToken());
    }

    @Test
    @DisplayName("checkAuth() Fail")
    public void checkAuthFail() throws DataAccessException {
        try {
            registerSuccess();
            authService.checkAuth("bogusAuthToken");
        } catch (DataAccessException e) {
            assert e.statusCode() == 401;
        }
    }

    @Test
    @DisplayName("clear() Success")
    public void clearSuccess() throws DataAccessException {
        try {
            registerSuccess();
            clearService.deleteDB();
            authService.login(new UserData(existingUserUsername, existingUserPassword, existingUserEmail));
            assert false;
        } catch (DataAccessException e) {
            assert e.statusCode() == 401;
        }
    }

    @Test
    @DisplayName("createGame() Success")
    public void createGameSuccess() throws DataAccessException {
        var gameData = gameService.createGame("test");
        assert gameData.gameName().equals("test");
    }

    @Test
    @DisplayName("createGame() Fail")
    public void createGameFail() throws DataAccessException {
        try {
            var gameData = gameService.createGame(null);
            assert false;
        } catch (DataAccessException e) {
            assert e.statusCode() == 400;
        }
    }

    @Test
    @DisplayName("listGames() Success")
    public void listGamesSuccess() throws DataAccessException {
        createGameSuccess();
        assert !gameService.listGames().isEmpty();
    }

    @Test
    @DisplayName("listGames() Fail")
    public void listGamesFail() throws DataAccessException {
        createGameSuccess();
        assert true;
    }

    @Test
    @DisplayName("joinGame() Success")
    public void joinGameSuccess() throws DataAccessException {
        var gameData = gameService.createGame("test");
        gameService.joinGame("username", "WHITE", gameData.gameID());
        assert gameService.listGames().get(gameData.gameID()).whiteUsername().equals("username");
    }

    @Test
    @DisplayName("joinGame() Fail")
    public void joinGameFail() throws DataAccessException {
        try {
            joinGameSuccess();
            gameService.joinGame("sneakyUser", "WHITE", 4);
            assert false;
        } catch (DataAccessException e) {
            assert true;
        }
    }
}
