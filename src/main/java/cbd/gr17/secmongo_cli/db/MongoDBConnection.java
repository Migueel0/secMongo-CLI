package cbd.gr17.secmongo_cli.db;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {

    public static MongoClient getMongoClient(String host, int port,String username, String password){
        String authConnectionString = String.format("mongodb://%s:%s@%s:%d", username, password, host, port);
        String connectionString = String.format("mongodb://%s:%d", host, port);

        String uri = !username.isEmpty() && !password.isEmpty() ? authConnectionString : connectionString;
        return MongoClients.create(uri);

    }

    public static MongoDatabase connect(String host, int port,String username, String password, String dbName) {
        MongoDatabase database = null;
        try {
            MongoClient mongoClient = getMongoClient(host, port, username, password);
            database = mongoClient.getDatabase(dbName);
            database.runCommand(new Document("ping", 1));
            System.out.println("\u001B[32m\nConnected to " + dbName + " at " + host + ":" + port + "\u001B[0m");
            return database;
        } catch (Exception e) {
            System.err.println("\nFailed to connect to MongoDB server: " + e.getMessage());
        }
        return database;
    }
}