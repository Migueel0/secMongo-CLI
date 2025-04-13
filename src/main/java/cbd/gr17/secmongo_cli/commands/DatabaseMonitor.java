package cbd.gr17.secmongo_cli.commands;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class DatabaseMonitor {

    private static final Set<String> VALID_OPERATION_TYPES = Set.of(
            "insert", "update", "replace", "delete", "invalidate", "drop", "rename", "dropDatabase");

    public static void monitorCollection(MongoDatabase database, String collectionName, String exportPath,
            String operationTypesRaw) {
        Set<String> selectedTypes = parseAndValidateOperationTypes(operationTypesRaw);
        if (selectedTypes == null)
            return;

        MongoCollection<Document> collection = database.getCollection(collectionName);

        printMonitorHeader(collectionName, selectedTypes, exportPath);

        collection.watch().forEach(change -> {
            String currentOp = change.getOperationType().getValue().toLowerCase();

            if (!selectedTypes.isEmpty() &&
                    selectedTypes.stream().noneMatch(op -> op.equalsIgnoreCase(currentOp))) {
                return;
            }

            String logEntry = formatChangeEvent(change);
            System.out.print(logEntry);

            if (exportPath != null && !exportPath.isBlank()) {
                writeToFile(exportPath, logEntry);
            }
        });
    }

    private static Set<String> parseAndValidateOperationTypes(String rawTypes) {
        Set<String> selectedTypes = new HashSet<>();

        if (rawTypes != null && !rawTypes.isBlank()) {
            String[] types = rawTypes.toLowerCase().split(",");
            for (String type : types) {
                type = type.trim();
                if (!VALID_OPERATION_TYPES.contains(type)) {
                    System.out.println("[!] Invalid operation type: '" + type + "'");
                    System.out.println("Valid types are: " + VALID_OPERATION_TYPES);
                    return null;
                }
                selectedTypes.add(type);
            }
        }

        return selectedTypes;
    }

    private static void printMonitorHeader(String collectionName, Set<String> selectedTypes, String exportPath) {
        System.out.println("=== MONITORING COLLECTION: " + collectionName + " ===");

        if (!selectedTypes.isEmpty()) {
            System.out.println("Filtering operation types: " + selectedTypes);
        } else {
            System.out.println("No operation type filter specified. Showing all events (insert, update, delete, etc.)");
        }

        if (exportPath != null && !exportPath.isBlank()) {
            System.out.println("Exporting changes to: " + Paths.get(exportPath).toAbsolutePath());
        }

        System.out.println("Press Ctrl + C to stop...\n");
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
