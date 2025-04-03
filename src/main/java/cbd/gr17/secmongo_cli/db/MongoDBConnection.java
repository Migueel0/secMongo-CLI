package cbd.gr17.secmongo_cli.db;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {

    public static MongoDatabase connect(String host, int port,String username, String password) {
        String authConnectionString = String.format("mongodb://%s:%s@%s:%d", username, password, host, port);
        String connectionString = String.format("mongodb://%s:%d", host, port);

        String uri = !username.isEmpty() && !password.isEmpty() ? authConnectionString : connectionString;
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = null;
        try {
            database = mongoClient.getDatabase("admin");
            database.runCommand(new Document("ping", 1));
            System.out.println("Connected to MongoDB server at " + host + ":" + port);
            return database;
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB server: " + e.getMessage());
        }
        return database;
    }
}