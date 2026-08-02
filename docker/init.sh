#!/bin/bash

echo "Waiting for MS SQL to be available ⏳"

# wait for MSSQL server to start
export STATUS=1
i=0

while [[ $STATUS -ne 0 ]] && [[ $i -lt 30 ]]; do
	i=$i+1
	/opt/mssql-tools/bin/sqlcmd -t 1 -U sa -P $SA_PASSWORD -Q "select 1" >> /dev/null
	STATUS=$?
done

if [ $STATUS -ne 0 ]; then
	echo "Error: MSSQL SERVER took more than thirty seconds to start up."
	exit 1
fi

echo =============== MSSQL STARTED                     ==========================

if [ ! -z $MSSQL_USER ]; then
    echo "MSSQL_USER: $MSSQL_USER"
else
    MSSQL_USER=storage
    echo "MSSQL_USER: $MSSQL_USER"
fi

if [ ! -z $MSSQL_PASSWORD ]; then
    echo "MSSQL_PASSWORD: $MSSQL_PASSWORD"
else
    MSSQL_PASSWORD=Password1!
    echo "MSSQL_PASSWORD: $MSSQL_PASSWORD"
fi

if [ ! -z $MSSQL_DB ]; then
    echo "MSSQL_DB: $MSSQL_DB"
else
    MSSQL_DB=storage
    echo "MSSQL_DB: $MSSQL_DB"
fi

echo =============== CREATING INIT DATA ==========================



cat <<-EOSQL > init.sql
CREATE DATABASE $MSSQL_DB;
GO
USE $MSSQL_DB;
GO
CREATE LOGIN $MSSQL_USER WITH PASSWORD = '$MSSQL_PASSWORD';
GO
CREATE USER $MSSQL_USER FOR LOGIN $MSSQL_USER;
GO
ALTER SERVER ROLE sysadmin ADD MEMBER [$MSSQL_USER];
GO
EOSQL

/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -t 30 -i"./init.sql" -o"/var/opt/mssql/data/initsqlout.log"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -t 30 -i"./db.sql" -o"/var/opt/mssql/data/initdbout.log"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -t 30 -i"./createTables.sql" -o"/var/opt/mssql/data/createtables.log"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -t 30 -i"./sp1_GetAccounts.sql" -o"/var/opt/mssql/data/initsp1.log"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -t 30 -i"./sp2_DeleteAccount.sql" -o"/var/opt/mssql/data/initsp2.log"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -t 30 -i"./sp3_DeleteProfile.sql" -o"/var/opt/mssql/data/initsp3.log"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -t 30 -i"./sp4_Login.sql" -o"/var/opt/mssql/data/initsp4.log"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -t 30 -i"./sp5_AddAccount.sql" -o"/var/opt/mssql/data/initsp5.log"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -t 30 -i"./sp6_AddProfile.sql" -o"/var/opt/mssql/data/initsp6.log"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -t 30 -i"./trigger_DeleteAccounts.sql" -o"/var/opt/mssql/data/inittrigger.log"
echo =============== INIT DATA CREATED ==========================
echo =============== MSSQL SERVER SUCCESSFULLY STARTED ==========================

