# secMongo-CLI

**secMongo-CLI** es una herramienta de línea de comandos diseñada para mejorar la seguridad de bases de datos MongoDB. Permite a los usuarios realizar auditorías de seguridad, monitorear operaciones activas y obtener estadísticas detalladas de las bases de datos.

---

## 📋 **Características**

- **Auditoría de seguridad**:
  - Escanea vulnerabilidades en la configuración de MongoDB.
  - Verifica la autenticación, el cifrado, la configuración de red y más.
  - Analiza colecciones para detectar problemas como inyección NoSQL o ejecución de JavaScript.

- **Monitoreo en tiempo real**:
  - Observa cambios en colecciones específicas utilizando `change streams`.
  - Filtra eventos por tipo de operación (insert, update, delete, etc.).
  - Exporta eventos a un archivo de registro.

- **Generación de reportes**:
  - Crea reportes de auditoría en formato Markdown.
  - Incluye recomendaciones de seguridad y un puntaje final.

- **Estadísticas detalladas**:
  - Muestra estadísticas de la base de datos, como el número de documentos, tamaño de colecciones, conexiones activas y más.

---

## 🚀 **Requisitos**

- **Java**: Versión 21 o superior.
- **MongoDB**: Recomendad versión 8.0.0 o superior.
- **Replica Set**: Requerido para monitoreo en tiempo real (`change streams`).
- **Maven**: Para compilar y ejecutar el proyecto.

---

## 🛠️ **Instalación**

1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/secmongo-cli.git
   cd secmongo-cli
   ```
2. Compila el proyecto
```bash
mvn clean install
```
3. Ejecuta la aplicación
```
mvn spring-boot:run
```

## 📖 Uso

**Comandos disponibles**
1. Prueba de conectividad a MongoDB
```bash
connect --host <host> --port <port> --username <username> --password <password> --dbName <database>
```
Realiza una prueba de conectividad con la base de datos.

2. Escáner de vulnerabilidades
```bash
scan --host <host> --port <port> --username <username> --password <password> --dbName <database>
```
Realiza un análisi de seguridad en la base de datos.

**Importante:** Para mejor funcionamiento del comando es recomendable usar un usuario con permisos de root.

3. Estadísticas
```bash
stats --host <host> --port <port> --username <username> --password <password> --dbName <database>
```
Muestra estadísticas detalladas de la base de datos.

4. Monitoreo de colección
```
monitor --host <host> --port <port> --username <username> --password <password> --dbName <database> --collectionName <collection> --exportPath <path> --operationTypes <types>
```
Monitorea cambios en una colección específica.
* Parmámetros opcionales:
    * *--exportPath:* Ruta para exportar los eventos a un archivo.
    * *--operationTypes:* Filtra eventos por tipo(insert,pudate,delete,etc...)

5. Generación automática de reportes
```
report --host <host> --port <port> --username <username> --password <password> --dbName <database> --exportPath <path>
```

## 📂 Estructura del proyecto

```
secmongo-cli/
├── src/
│   ├── main/
│   │   ├── java/cbd/gr17/secmongo_cli/
│   │   │   ├── cli/                # Comandos de la CLI
│   │   │   ├── commands/           # Lógica de auditoría y monitoreo
│   │   │   ├── config/             # Configuración de la CLI
│   │   │   ├── db/                 # Conexión a MongoDB
│   │   ├── resources/              # Archivos de configuración
│   ├── test/                       
├── target/                         # Archivos generados por Maven
├── README.md                       # Documentación del proyecto
├── pom.xml                         # Configuración de Maven
```

## 📋Ejemplos de uso:
**Ejemplo de comando *scan***:
```
scan --username root --password root --dbName myDatabase
```
* Salida Esperada:
```
===================================================
Running security checks on myDatabase database...
===================================================

DATABASE USERS CHECKS
--------------------------------------------------
Checking user roles...
--------------------------------------------------
[✔] User 'admin' has roles assigned.

COLLECTIONS CHECKS
--------------------------------------------------
Checking database collections...
--------------------------------------------------
Scanning collection: users
--------------------------------------------------
[✔] Collection 'users' has a validator configured.
[✔] The collection users is not vulnerable to NoSQL Injection.
[✔] JavaScript execution is blocked.
[✔] Collection 'users' has 2 index(es).
[✔] Collection 'users' is capped.

Final mark: 90%
```

**Ejemplo de comando *report***:
```
report --host localhost --port 27017 --username admin --password admin123 --dbName admin --exportPath ./report.md
```
* Salida esperada:
```
report generated at: ./report.md
```

## 🧠 Recomendaciones de seguridad
* **Habilitar SSL/TLS:** Protege los datos en tránsito.
* **Configurar IP Binding:** Limita las conexiones a direcciones IP específicas.
* **Habilitar auditoría:** Registra eventos administrativos y de seguridad.
* **Actualizar MongoDB:** Usa siempre la última versión estable.

## 📧 Contacto
1.  Miguel Galán Lerate
* **Email**: miggaller@alum.us.es
2. David Fuentelsaz Rodríguez
* **Email**: davfuerod@alum.us.es
