USE storage;
GO
DROP PROCEDURE IF EXISTS dbo.DeleteAccount;
GO
CREATE PROCEDURE dbo.DeleteAccount
(@PID INT)
AS
BEGIN
    DELETE FROM dbo.accounts
    WHERE id=@PID;
END