package cbd.gr17.secmongo_cli.db;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {

    private MongoDatabase database;

    public void connect(String host, int port, String dbName) {
        String connectionString = String.format("mongodb://%s:%d", host, port);
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            database = mongoClient.getDatabase(dbName);
            database.runCommand(new Document("ping", 1));
            System.out.println("Connected to MongoDB server at " + host + ":" + port);
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB server: " + e.getMessage());
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}