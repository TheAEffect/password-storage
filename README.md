# PasswordStorage

> Developed as part of a university course at Hochschule Esslingen.

## Prerequisites
Before setting up the project, make sure the following software is installed on your system:
- **JDK** (version 11 or higher)
- **Maven**
- **Docker** and **Docker Compose**

## Setting up the project

<details>
<summary><h3>Local</h3></summary>

To run everything locally, follow these steps:

1. If present, delete the `data` folder inside the `docker` folder. This folder is generated after each build and indicates whether the tables have already been created or still need to be set up.

2. Create the `.env` file (based on `.env.example`) and fill in your own credentials:

   ```sh
   # System-Admin password
   SA_PASSWORD=your_sa_password_here

   DB_HOST=127.0.0.1
   DB_PORT=1433
   DB_NAME=storage
   DB_USER=storage
   DB_PASS=your_db_password_here
   ```

   > Note: If port `1433` causes conflicts on your system, you can change it (e.g. to `14331`) in both `docker-compose.yml` and your `.env`.

3. Navigate into the `docker` folder and start the container with:
   ```sh
   docker-compose up
   ```
   This runs the scripts `entrypoint.sh`, `init.sh` and `db.sql`, and creates a Docker image containing the database. Any required data is stored in the newly created `data` folder inside `docker`.

4. Start the application. `DB.java` reads the connection details directly from the `.env` file.

</details>

**Environment variables:**

| Variable | Description |
|---|---|
| `SA_PASSWORD` | Password for the SQL Server system administrator account |
| `DB_HOST` | Address at which the database can be reached. Default: `127.0.0.1` |
| `DB_PORT` | Port at which the database can be reached. Default: `1433` |
| `DB_NAME` | Name of the database. Default: `storage` |
| `DB_USER` | Username for the database. Default: `storage` |
| `DB_PASS` | Password for the database user |


<details>
   <summary><h3>Screenshots</h3></summary>


   
   Loginscreen
   
   <img width="40%" alt="image" src="https://github.com/user-attachments/assets/53f6533f-bac2-4388-a717-c64f29428c17" />
   
   All Accounts
   
   <img width="60%" alt="image" src="https://github.com/user-attachments/assets/9a65ea80-3330-4210-8055-b8efb16150fd" />


</details>

