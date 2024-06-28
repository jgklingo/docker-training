
import server.Server;

public class Main {
    public static void main(String[] args) {
        try {
            Server server = new Server();
            int port = server.run(8080);
            System.out.printf("Server started on port %d\n", port);
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}