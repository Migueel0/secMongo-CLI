package cbd.gr17.secmongo_cli.commands;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class DatabaseMonitor {

    public static void monitorCollection(MongoDatabase database, String collectionName, String exportPath) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);

            System.out.println("=== MONITORING COLLECTION: " + collectionName + " ===");
            if (exportPath != null && !exportPath.isBlank()) {
                System.out.println("Exporting changes to: " + Paths.get(exportPath).toAbsolutePath());
            }
            System.out.println("Press Ctrl + C to stop...\n");

            collection.watch().forEach(change -> {
                String logEntry = formatChangeEvent(change);
                System.out.print(logEntry);

                if (exportPath != null && !exportPath.isBlank()) {
                    writeToFile(exportPath, logEntry);
                }
            });

        } catch (Exception e) {
            System.out.println("[!] Failed to monitor collection '" + collectionName + "': " + e.getMessage());
        }
    }

    private static String formatChangeEvent(ChangeStreamDocument<Document> change) {
        StringBuilder log = new StringBuilder();

        log.append("---------- CHANGE DETECTED ----------\n");
        log.append("• Operation Type : ").append(change.getOperationType()).append("\n");

        Document docKey = change.getDocumentKey() != null ? Document.parse(change.getDocumentKey().toJson()) : null;
        if (docKey != null && docKey.containsKey("_id")) {
            log.append("• Document _id   : ").append(docKey.get("_id")).append("\n");
        }

        log.append("• Timestamp      : ").append(change.getClusterTime()).append("\n");

        Document fullDoc = change.getFullDocument();
        if (fullDoc != null) {
            log.append("• Full Document  : ").append(fullDoc.toJson()).append("\n");
        }

        if (change.getUpdateDescription() != null) {
            log.append("• Updated Fields : ").append(change.getUpdateDescription().getUpdatedFields()).append("\n");
            log.append("• Removed Fields : ").append(change.getUpdateDescription().getRemovedFields()).append("\n");
        }

        log.append("-------------------------------------\n\n");
        return log.toString();
    }

    private static void writeToFile(String exportPath, String logEntry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportPath, true))) {
            writer.write(logEntry);
        } catch (IOException e) {
            System.out.println("[!] Could not write to log file: " + e.getMessage());
        }
    }
}
