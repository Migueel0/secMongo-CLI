package cbd.gr17.secmongo_cli.cli;

import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.stereotype.Component;
import cbd.gr17.secmongo_cli.db.MongoDBConnection;

@Component
public class cli implements CommandLineRunner {

    final String initalMessage = """
            -------------------------------------------------
            Welcome to SecMongo CLI!
            -------------------------------------------------
            Please enter the following information to connect to your MongoDB server:
            """;

    private MongoDBConnection mongoDBConnection = new MongoDBConnection();

    @Override
    @ShellMethod(value = "Connect to MongoDB", key = "connect")
    public void run(String... args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println(initalMessage);

            System.out.println("Host [default: localhost]: ");
            String host = scanner.nextLine().trim();
            if (host.isEmpty()) host = "localhost";

            System.out.println("Port [default: 27017]: ");
            String portInput = scanner.nextLine().trim();
            int port = portInput.isEmpty() ? 27017 : Integer.parseInt(portInput);

            System.out.println("Database name: ");
            String dbName = scanner.nextLine().trim();

            mongoDBConnection.connect(host, port, dbName);
        }
    }
}
