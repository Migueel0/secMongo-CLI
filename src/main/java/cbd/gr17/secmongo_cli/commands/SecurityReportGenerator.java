package cbd.gr17.secmongo_cli.commands;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.Document;

import com.mongodb.client.MongoDatabase;

public class SecurityReportGenerator {

    public static String generateReport(MongoDatabase database, MongoDatabase adminDb, String dbName) {
        StringBuilder report = new StringBuilder();

        report.append("# MongoDB Security Audit Report\n\n");

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String host = "Unknown";
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (Exception ignored) {
        }

        report.append("**📅 Date:** ").append(timestamp).append("\n");
        report.append("**💻 Host:** ").append(host).append("\n");
        report.append("**🗂️ Database:** ").append(dbName).append("\n\n");

        report.append("---\n\n");

        report.append("## 🔐 Network & Access Configurations\n\n");
        report.append("```\n").append(captureOutput(() -> {
            VulnerabilityScanner.checkTLS(database);
            VulnerabilityScanner.checkIPBinding(database);
            VulnerabilityScanner.checkAuthentication(database);
        })).append("```\n\n");

        report.append("## 👥 User & Role Checks\n\n");
        report.append("```\n").append(captureOutput(() -> VulnerabilityScanner.checkUsersWithNoRoles(database)))
                .append("```\n\n");

        report.append("## 🛡️ Injection & JS Execution\n\n");
        report.append("```\n").append(captureOutput(() -> VulnerabilityScanner.scanAllCollections(database)))
                .append("```\n\n");

        report.append("## 🧪 Input Validation\n\n");
        report.append("```\n").append(captureOutput(() -> VulnerabilityScanner.checkWireObjectCheck(database)))
                .append("```\n\n");

        report.append("## 📊 Collection & Indexes\n\n");
        report.append("```\n").append(captureOutput(() -> {
            VulnerabilityScanner.checkCollectionSizes(database);
            VulnerabilityScanner.checkIndexes(database);
            VulnerabilityScanner.checkTTLIndexes(database);
            VulnerabilityScanner.checkCappedCollections(database);
        })).append("```\n\n");

        report.append("## ⚙️ Critical Configurations\n\n");
        report.append("```\n").append(captureOutput(() -> {
            VulnerabilityScanner.checkCriticalConfig(database);
            VulnerabilityScanner.checkAuditLog(database);
        })).append("```\n\n");

        report.append("## ✅ Final Summary\n\n");
        report.append("```\n").append(captureOutput(() -> VulnerabilityScanner.runSecurityChecks(database)))
                .append("```\n");

        report.append(generateRecommendations(database, adminDb));

        report.append(generateScoreSummary(
                VulnerabilityScanner.getTotalChecks(),
                VulnerabilityScanner.getSuccessChecks()));

        return report.toString();
    }

    /**
     * Captures anything printed to the standard output (System.out) during the
     * execution of the provided code block and returns it as a String.
     *
     * @param block A Runnable containing the code that prints output to System.out
     * @return A String containing all the captured output from System.out
     */
    private static String captureOutput(Runnable block) {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream oldOut = System.out;
        System.setOut(new java.io.PrintStream(baos));
        try {
            block.run();
        } finally {
            System.setOut(oldOut);
        }
        return baos.toString();
    }

    /**
     * Generates a set of security recommendations based on the configuration and
     * user roles
     * of the provided MongoDB databases.
     *
     * @param db      The MongoDB database to analyze user roles.
     * @param adminDb The MongoDB `admin` database to analyze server configuration.
     * @return A string containing the generated security recommendations.
     */
    private static String generateRecommendations(MongoDatabase db, MongoDatabase adminDb) {
        StringBuilder recs = new StringBuilder();

        recs.append("## 🧠 Recommendations\n\n");

        try {
            Document cmdLineOpts = adminDb.runCommand(new Document("getCmdLineOpts", 1));
            String config = cmdLineOpts.toJson();

            if (!config.contains("sslMode") || config.contains("disabled")) {
                recs.append("- 🔐 **Enable SSL/TLS** to protect data in transit.\n");
            }
            if (config.contains("bindIp") && config.contains("0.0.0.0")) {
                recs.append("- 🌐 **Avoid using bindIp = 0.0.0.0**. Limit MongoDB to localhost or trusted IPs.\n");
            }
            if (config.contains("--nojournal") || !config.contains("journal")) {
                recs.append("- 💾 **Enable journaling** to prevent data loss in case of crashes.\n");
            }
            if (!config.contains("auditLog")) {
                recs.append("- 🕵️ **Enable auditing** to track administrative and security-related operations.\n");
            }
        } catch (Exception e) {
            recs.append(
                    "- ⚠ Could not analyze server configuration. Make sure you're connected to the `admin` database.\n");
        }

        try {
            Document result = db.runCommand(new Document("usersInfo", 1));
            var users = result.getList("users", Document.class);
            for (Document user : users) {
                if (!user.containsKey("roles") || user.getList("roles", Document.class).isEmpty()) {
                    recs.append("- 👤 **Assign roles** to user `").append(user.getString("user")).append("`\n");
                }
            }
        } catch (Exception e) {
            recs.append("- ⚠ Could not verify user roles for recommendations.\n");
        }

        if (recs.toString().endsWith("Recommendations\n\n")) {
            recs.append("- ✔ No critical recommendations. Your configuration looks good!\n");
        }

        return recs.toString();
    }

    private static String generateScoreSummary(int total, int passed) {
        int percentage = (int) ((passed * 100.0) / total);
        String stars;
        String label;

        if (percentage <= 30) {
            stars = "⭐☆☆☆☆";
            label = "🔴 Very Low";
        } else if (percentage <= 50) {
            stars = "⭐⭐☆☆☆";
            label = "🟠 Low";
        } else if (percentage <= 70) {
            stars = "⭐⭐⭐☆☆";
            label = "🟡 Moderate";
        } else if (percentage <= 85) {
            stars = "⭐⭐⭐⭐☆";
            label = "🟢 Good";
        } else {
            stars = "⭐⭐⭐⭐⭐";
            label = "✅ Excellent";
        }

        return """
                ## 🎯 Final Security Score

                **Total checks:** %d
                **Passed checks:** %d
                **Score:** %d%%

                **Rating:** %s (%s)

                """.formatted(total, passed, percentage, stars, label);
    }

}
