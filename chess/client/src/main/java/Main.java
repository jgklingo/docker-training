import ui.Repl;

public class Main {
    public static void main(String[] args) {
        var serverUrl = "http://host.docker.internal:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        new Repl(serverUrl).run();
    }
}