# PasswordStorage

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
