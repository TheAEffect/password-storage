DROP PROCEDURE IF EXISTS dbo.Schnell_DeleteAccount;
GO
CREATE PROCEDURE dbo.Schnell_DeleteAccount
(@PID INT)
AS
BEGIN
    DELETE FROM dbo.Schnell_accounts
    WHERE id=@PID;
END
