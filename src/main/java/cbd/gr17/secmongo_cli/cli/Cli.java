package cbd.gr17.secmongo_cli.cli;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.mongodb.client.MongoDatabase;

import cbd.gr17.secmongo_cli.commands.DatabaseStats;
import cbd.gr17.secmongo_cli.commands.VulnerabilityScanner;
import cbd.gr17.secmongo_cli.db.MongoDBConnection;

@ShellComponent()
public class Cli {

    @ShellMethod(value = "Connection test to MongoDB", key = "connect")
    public void connect(
            @ShellOption(defaultValue = "localhost", help = "MongoDB host (default: localhost)") String host,
            @ShellOption(defaultValue = "27017", help = "MongoDB port (default: 27017)") int port,
            @ShellOption(defaultValue = "", help = "MongoDB username") String username,
            @ShellOption(defaultValue = "", help = "MongoDB password") String password) {
        try {
            MongoDBConnection.connect(host, port, username, password);
            System.out.println("Connected successfully");
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    @ShellMethod(value = "Scan MongoDB vulnerabilities", key = "scan")
    public void scan(
            @ShellOption(defaultValue = "localhost", help = "MongoDB host (default: localhost)") String host,
            @ShellOption(defaultValue = "27017", help = "MongoDB port (default: 27017)") int port,
            @ShellOption(defaultValue = "", help = "MongoDB username") String username,
            @ShellOption(defaultValue = "", help = "MongoDB password") String password) {
        try {
            MongoDatabase database = MongoDBConnection.connect(host, port, username, password);
            VulnerabilityScanner.runSecurityChecks(database);
            System.out.println("Scann finished");
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    @ShellMethod(value = "Show statistics about the MongoDB database", key = "stats")
    public void stats(
            @ShellOption(defaultValue = "localhost") String host,
            @ShellOption(defaultValue = "27017") int port,
            @ShellOption(defaultValue = "") String username,
            @ShellOption(defaultValue = "") String password) {

        try {
            MongoDatabase db = MongoDBConnection.connect(host, port, username, password);
            DatabaseStats.printFullStats(db);
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

}
