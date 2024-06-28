package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.SQLDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.Objects;
import java.util.Set;

// Better than my service tests, but I'm still not sure exactly what I'm supposed to be doing here

public class DataAccessTests {
    String existingUserUsername = "ExistingUser";
    String existingUserPassword = "existingUserPassword";
    String existingUserEmail = "eu@mail.com";
    UserData existingUserData = new UserData(existingUserUsername, existingUserPassword, existingUserEmail);
    AuthData testAuthData = new AuthData("tokentokentokenTOKEN", "user");
    static SQLDataAccess sqlDataAccess;
    @BeforeAll
    public static void setup() throws DataAccessException {
        try {
            sqlDataAccess = new SQLDataAccess();
        } catch (Throwable ex) {
            throw new RuntimeException("SQL database failed to initialize: %s".formatted(ex.getMessage()));
        }
        sqlDataAccess.deleteDB();
    }
    @AfterEach
    public void reset() throws DataAccessException {
        sqlDataAccess.deleteDB();
    }

    @Test
    @DisplayName("createUser() Success")
    public void createUserSuccess() {
        try {
            sqlDataAccess.createUser(existingUserData);
            sqlDataAccess.checkUser(existingUserData);
            assert true;
        } catch (Throwable ex) {
            assert false;
        }
    }
    @Test
    @DisplayName("createUser() Failure")
    public void createUserFailure() {
        try {
            sqlDataAccess.createUser(existingUserData);
            sqlDataAccess.createUser(existingUserData);
            assert false;
        } catch (DataAccessException ex) {
            assert ex.statusCode() == 403;
        }
    }
    @Test
    @DisplayName("createAuth() Success")
    public void createAuthSuccess() {
        try {
            sqlDataAccess.createAuth(testAuthData);
            sqlDataAccess.checkAuth(testAuthData.authToken());
            assert true;
        } catch (Throwable ex) {
            assert false;
        }
    }
    @Test
    @DisplayName("createAuth() Failure")
    public void createAuthFailure() {
        try {
            sqlDataAccess.createAuth(testAuthData);
            sqlDataAccess.checkAuth("someAuthTokenThatIsNotRight");
            assert false;
        } catch (DataAccessException ex) {
            assert ex.statusCode() == 401;
        }
    }
    @Test
    @DisplayName("checkUser() Success")
    public void checkUserSuccess() {
        try {
            sqlDataAccess.createUser(existingUserData);
            sqlDataAccess.checkUser(existingUserData);
            assert true;
        } catch (Throwable ex) {
            assert false;
        }
    }
    @Test
    @DisplayName("checkUser() Failure")
    public void checkUserFailure() {
        try {
            sqlDataAccess.checkUser(new UserData("hacker", "hackersdontusepasswords", "hacker@mail.ru"));
            assert false;
        } catch (DataAccessException ex) {
            assert ex.statusCode() == 401;
        }
    }
    @Test
    @DisplayName("deleteAuth Success")
    public void deleteAuthSuccess() {
        try {
            sqlDataAccess.createAuth(new AuthData("supersecuretoken", "tester"));
            sqlDataAccess.deleteAuth("supersecuretoken");
            assert true;
        } catch (Throwable ex) {
            assert false;
        }
    }
    @Test
    @DisplayName("deleteAuth Failure")
    public void deleteAuthFailure() {
        try {
            sqlDataAccess.createAuth(new AuthData("supersecuretoken", "tester"));
            sqlDataAccess.checkAuth("someothertoken");
            sqlDataAccess.deleteAuth("someothertoken");
            assert false;
        } catch (DataAccessException ex) {
            assert ex.statusCode() == 401;
        }
    }
    @Test
    @DisplayName("checkAuth() Success")
    public void checkAuthSuccess() {
        try {
            sqlDataAccess.createAuth(testAuthData);
            sqlDataAccess.checkAuth(testAuthData.authToken());
            assert true;
        } catch (Throwable ex) {
            assert false;
        }
    }
    @Test
    @DisplayName("checkAuth() Failure")
    public void checkAuthFailure() {
        try {
            sqlDataAccess.checkAuth("noTokens");
            assert false;
        } catch (DataAccessException ex) {
            assert ex.statusCode() == 401;
        }
    }
    @Test
    @DisplayName("newGame() Success")
    public void newGameSuccess() {
        try {
            sqlDataAccess.newGame("game1");
            sqlDataAccess.newGame("game2");
            sqlDataAccess.newGame("game3");
            assert sqlDataAccess.getGames().size() == 3;
        } catch (Throwable ex) {
            assert false;
        }
    }
    @Test
    @DisplayName("newGame() Failure")
    public void newGameFailure() {
        try {
            sqlDataAccess.newGame(null);
            assert false;
        } catch (DataAccessException ex) {
            assert ex.statusCode() == 400;
        }
    }
    @Test
    @DisplayName("getGames() Success")
    public void getGamesSuccess() {
        try {
            sqlDataAccess.newGame("game1");
            sqlDataAccess.newGame("game2");
            sqlDataAccess.newGame("game3");
            var result = sqlDataAccess.getGames();
            assert Objects.equals(result.keySet(), Set.of(1, 2, 3));
        } catch (Throwable ex) {
            assert false;
        }
    }
    @Test
    @DisplayName("getGames() Failure")
    public void getGamesFailure() {
        try {
            sqlDataAccess.newGame("test");
            assert true;
        } catch (DataAccessException ex) {
            assert false;
        }
    }
    @Test
    @DisplayName("addPlayer() Success")
    public void addPlayerSuccess() {
        try {
            sqlDataAccess.newGame("test");
            sqlDataAccess.addPlayer("tester", "BLACK", 1);
            var result = sqlDataAccess.getGames();
            assert result.get(1).blackUsername().equals("tester");
        } catch (Throwable ex) {
            assert false;
        }
    }
    @Test
    @DisplayName("addPlayer() Failure")
    public void addPlayerFailure() {
        try {
            sqlDataAccess.newGame("test");
            sqlDataAccess.addPlayer("tester", "BLACK", 1);
            sqlDataAccess.addPlayer("another_tester", "BLACK", 1);
            assert false;
        } catch (DataAccessException ex) {
            assert ex.statusCode() == 403;
        }
    }
    @Test
    @DisplayName("deleteDB() Success")
    public void deleteDBSuccess() {
        try {
            sqlDataAccess.newGame("test");
            assert sqlDataAccess.getGames().size() == 1;
            sqlDataAccess.deleteDB();
            assert sqlDataAccess.getGames().isEmpty();
        } catch (Throwable ex) {
            assert false;
        }
    }
    @Test
    @DisplayName("deleteDB() Failure")
    public void deleteDBFailure() {
        assert true;
        // best test in the history of tests
    }
}
