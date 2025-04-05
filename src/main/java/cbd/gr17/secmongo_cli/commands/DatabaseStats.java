package cbd.gr17.secmongo_cli.commands;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DatabaseStats {
   
    public static void printStats(MongoDatabase database) {
        try {
            System.out.println("\n=== DATABASE STATISTICS ===");
    
            int totalDocs = 0;
    
            for (String collectionName : database.listCollectionNames()) {
                MongoCollection<Document> collection = database.getCollection(collectionName);
                long count = collection.countDocuments();
    
                Document stats = database.runCommand(new Document("collStats", collectionName));
                long sizeInBytes = ((Number) stats.get("size")).longValue();
                double sizeInMB = sizeInBytes / (1024.0 * 1024.0);
    
                System.out.printf("   - %-25s %6d documents (%5.2f MB)%n", collectionName + ":", count, sizeInMB);
                totalDocs += count;
            }
    
            int totalCollections = database.listCollectionNames().into(new ArrayList<>()).size();
    
            System.out.println("\nTotal collections : " + totalCollections);
            System.out.println("Total documents   : " + totalDocs);
        } catch (Exception e) {
            System.out.println("[!] Could not retrieve database statistics: " + e.getMessage());
        }
    }
    

    public static void printConnectionStats(MongoDatabase database) {
        try {
            Document status = database.runCommand(new Document("serverStatus", 1));
            Document connections = (Document) status.get("connections");
            double uptimeSeconds = ((Number) status.get("uptime")).doubleValue();
    
            long uptimeHours = (long) (uptimeSeconds / 3600);
            long uptimeMinutes = (long) ((uptimeSeconds % 3600) / 60);
            long uptimeSecs = (long) (uptimeSeconds % 60);
    
            System.out.println("\n=== CONNECTION STATISTICS ===");
            System.out.println("   - Current connections       : " + connections.get("current"));
            System.out.println("   - Available connections     : " + connections.get("available"));
            System.out.println("   - Total created connections : " + connections.get("totalCreated"));
            System.out.printf("   - Uptime                    : %d h %d min %d sec%n", uptimeHours, uptimeMinutes, uptimeSecs);
        } catch (Exception e) {
            System.out.println("[!] Could not retrieve connection stats: " + e.getMessage());
        }
    }

    public static void printSystemMetrics(MongoDatabase database) {
        try {
            Document status = database.runCommand(new Document("serverStatus", 1));
    
            
            Object localTime = status.get("localTime");
            System.out.println("\n=== SYSTEM INFORMATION ===");
            System.out.println("   - Server time               : " + localTime.toString());
    
            
            Document opCounters = (Document) status.get("opcounters");
            System.out.println("\n=== OPERATION COUNTERS ===");
            System.out.println("   - Inserts  : " + opCounters.get("insert"));
            System.out.println("   - Queries  : " + opCounters.get("query"));
            System.out.println("   - Updates  : " + opCounters.get("update"));
            System.out.println("   - Deletes  : " + opCounters.get("delete"));
            System.out.println("   - Commands : " + opCounters.get("command"));
    
            
            Document mem = (Document) status.get("mem");
            System.out.println("\n=== MEMORY USAGE ===");
            System.out.println("   - Resident Memory (MB) : " + mem.get("resident"));
            System.out.println("   - Virtual Memory (MB)  : " + mem.get("virtual"));
    
        } catch (Exception e) {
            System.out.println("[!] Could not retrieve system metrics: " + e.getMessage());
        }
    }

    public static void printFullStats(MongoDatabase database){
        printStats(database);
        printConnectionStats(database);
        printSystemMetrics(database);
    }
    

}
