# PasswordStorage

**Externe Bibliotheken**<br>
Für das Projekt wurden 4 externe Bibliotheken verwendet. Diese wären "commons-codec-1.15.jar", "commons-io-2.10.0.jar", "image4j-0.7.2.jar" und "mssql-jdbc-9.2.1.jre8.jar". Da ich nicht weiß ob die Bibliotheken des Projekts in "External Libraries" mit hochgeladen werden, habe ich zusätzlich einen Ordner "libs" im Projekt ertstellt, welche alle .jar Dateien enthält. Diese müssen dem Projekt hinzugefügt werden.

########################################

**SQL Dateien**<br>
Da ursprünglich mit Docker gearbeitet wurde, waren die SQL Dateien auch im "docker" Ordner enthalten. Nun, da auch der Remote-Zugirff funktioniert, ist Docker nicht mehr vonnöten. Somit habe ich auch hier einen extra Ordner "SQL Files" erstellt, welcher alle .sql Datein beinhaltet. Zunächst muss db.sql ausgeführt werden um die Tabellen zu erstellen. Anschließend können alle "Stored Procedures" und der "Trigger" erstellt werden.

########################################

**Remote:**<br>
Unter Verwendung der Datenbank des Hochschulservers muss lediglich das Projekt gestartet werden die Verbindungsangaben sind bereits in der DB.java
gespeichert.
Einziger Nachteil, dass die Favicons der Webseiten nicht angezeigt werden können (siehe Dokumentation).

########################################

**Lokal:**<br>
Da zunächst geplant war Docker für die MS-SQL Datenbank zu verwendet findet sich der Docker mit Installationsskript noch immer hier.
Um das ganze also lokal nutzen zu können muss zunächst falls vorhanden der Ordner data innerhalb des Ordners docker gelöscht werden. Dieser wird nach jedem Build erzeugt und stellt fest ob bereits die Tabellen angelegt wurden oder ob dies noch zu tun ist.

Nun wird in den Ordner docker gewechselt und dort das Dockerfile mit folgendem Befehl ausgeführt: docker-compose up

Da es bei mir zu komplikationen geführt hat den Port 1433 zu verwenden habe ich den Port 14331 in der docker-compose.yml verwendet.

Dadurch werden die Skripe entrypoint.sh, init.sh und db.sql ausgeführt und ein Docker Image mit enthaltener Datenbank erzeugt.
Erforderliches wird in dem neu erstellten Ordner data innerhalb des Ordners docker erstellt.

Um nun also auf die Datenbank zuzugreifen muss man folgenden Daten eingeben:
Server type: Database Engine
Server name: 127.0.0.1,14331
Authentication: SQL Server Authentication
Login: storage
Password: Password1!

Das Skript muss in der DB.java Datei ebenfalls an die Verbindung angepasst werden.
String dbHost = "127.0.0.1"
int dbPort = 14331
String dbName = "storage"
String dbUser = "storage"
String dbPass = "Password1!"
