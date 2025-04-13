package cbd.gr17.secmongo_cli.cli;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.mongodb.client.MongoDatabase;

import cbd.gr17.secmongo_cli.commands.DatabaseMonitor;
import cbd.gr17.secmongo_cli.commands.DatabaseStats;
import cbd.gr17.secmongo_cli.commands.VulnerabilityScanner;
import cbd.gr17.secmongo_cli.db.MongoDBConnection;

@ShellComponent()
public class Cli {

    @ShellMethod(value = "Connection test to MongoDB", key = "connect")
    public void connect(
            @ShellOption(defaultValue = "localhost") String host,
            @ShellOption(defaultValue = "27017") int port,
            @ShellOption(defaultValue = "") String username,
            @ShellOption(defaultValue = "") String password,
            @ShellOption(defaultValue = "admin") String dbName) {

        try {
            MongoDBConnection.connect(host, port, username, password, dbName);
            System.out.println("Connection successful");
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    @ShellMethod(value = "Scan MongoDB vulnerabilities", key = "scan")
    public void scan(
            @ShellOption(defaultValue = "localhost") String host,
            @ShellOption(defaultValue = "27017") int port,
            @ShellOption(defaultValue = "") String username,
            @ShellOption(defaultValue = "") String password,
            @ShellOption(defaultValue = "admin") String dbName) {

        try {
            MongoDatabase database = MongoDBConnection.connect(host, port, username, password, dbName);
            VulnerabilityScanner.runSecurityChecks(database);
            System.out.println("Scan finished");
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    @ShellMethod(value = "Show full statistics about the MongoDB database", key = "stats")
    public void stats(
            @ShellOption(defaultValue = "localhost") String host,
            @ShellOption(defaultValue = "27017") int port,
            @ShellOption(defaultValue = "") String username,
            @ShellOption(defaultValue = "") String password,
            @ShellOption(defaultValue = "admin") String dbName) {

        try {
            MongoDatabase db = MongoDBConnection.connect(host, port, username, password, dbName);
            if (db == null) {
                System.out.println("Could not connect to MongoDB.");
                return;
            }
            DatabaseStats.printFullStats(db);
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    @ShellMethod(value = "Monitor changes in a specific MongoDB collection", key = "monitor")
    public void monitor(
            @ShellOption(defaultValue = "localhost") String host,
            @ShellOption(defaultValue = "27017") int port,
            @ShellOption(defaultValue = "") String username,
            @ShellOption(defaultValue = "") String password,
            @ShellOption(help = "Database name to connect to") String dbName,
            @ShellOption(help = "Collection name to monitor") String collectionName,
            @ShellOption(defaultValue = "", help = "Optional path to export events to a log file") String exportPath,
            @ShellOption(defaultValue = "",  help = "Optional filters: insert, update, delete, replace, invalidate, drop, rename, dropDatabase") String operationTypes) {
    
        try {
            MongoDatabase db = MongoDBConnection.connect(host, port, username, password, dbName);
            if (db == null) {
                System.out.println("Could not connect to MongoDB.");
                return;
            }
            DatabaseMonitor.monitorCollection(db, collectionName, exportPath, operationTypes);
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
    

}
