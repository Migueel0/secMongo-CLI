# MongoDB Security Audit Report

**📅 Date:** 2025-04-25 15:44:23
**💻 Host:** miguel
**🗂️ Database:** admin

---

## 🔐 Network & Access Configurations

```
[⚠] SSL/TLS is not enabled.
[✔] MongoDB is bound to specific IP addresses.
[✔] Authentication is enabled.
[⚠] Encryption is not configured.
[✔] MongoDB version is up to date
[✔] Database path is configured.
[⚠] Auditing is not enabled.
```

## 👥 User & Role Checks

```
[✔] User 'adminUsuario' has roles assigned.
[✔] User 'root' has roles assigned.
[✔] User 'user' has roles assigned.
[✔] User 'user1' has roles assigned.
```

## ✅ Final Summary

```
===================================================
Running security checks on admin database...
===================================================

CONFIGURATION CHECKS
Note: The following checks are only performed on the 'admin' database.
--------------------------------------------------
Checking network security...
--------------------------------------------------
[⚠] SSL/TLS is not enabled.
[✔] MongoDB is bound to specific IP addresses.

Checking auth configuration...
--------------------------------------------------
[✔] Authentication is enabled.

Checking encryption...
--------------------------------------------------
[⚠] Encryption is not configured.

Checking server version...
--------------------------------------------------
[✔] MongoDB version is up to date

Checking database path...
--------------------------------------------------
[✔] Database path is configured.

Checking auditing...
--------------------------------------------------
[⚠] Auditing is not enabled.

DATABASE USERS CHECKS
--------------------------------------------------
Checking user roles...
--------------------------------------------------
[✔] User 'adminUsuario' has roles assigned.
[✔] User 'root' has roles assigned.
[✔] User 'user' has roles assigned.
[✔] User 'user1' has roles assigned.
```
## 🧠 Recommendations

- 🔐 **Enable SSL/TLS** to protect data in transit.
- 💾 **Enable journaling** to prevent data loss in case of crashes.
- 🕵️ **Enable auditing** to track administrative and security-related operations.
## 🎯 Final Security Score

**Total checks:** 11
**Passed checks:** 8
**Score:** 72%

**Rating:** ⭐⭐⭐⭐☆ (🟢 Good)

