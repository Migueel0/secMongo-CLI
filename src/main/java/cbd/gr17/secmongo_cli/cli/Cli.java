package cbd.gr17.secmongo_cli.cli;
import org.springframework.boot.CommandLineRunner;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import cbd.gr17.secmongo_cli.db.MongoDBConnection;

@ShellComponent()
public class Cli implements CommandLineRunner {

    final String initialMessage = """
            -------------------------------------------------
            Welcome to SecMongo CLI!
            -------------------------------------------------
            """;

    private final MongoDBConnection mongoDBConnection = new MongoDBConnection();


    @Override
    public void run(String... args) throws Exception {
        System.out.println(initialMessage);
    }


    @ShellMethod(value = "Connection test to MongoDB", key = "connect")
    public void connect(
            @ShellOption(defaultValue = "localhost", help = "MongoDB host (default: localhost)") String host,
            @ShellOption(defaultValue = "27017", help = "MongoDB port (default: 27017)") int port){
        try {
            mongoDBConnection.connect(host, port);
            System.out.println("Connected successfully");
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
