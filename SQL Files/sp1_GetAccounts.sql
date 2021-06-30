DROP PROCEDURE IF EXISTS dbo.Schnell_GetAccounts;
GO
CREATE PROCEDURE GetAccounts
(@PID INT)
AS
BEGIN
    SELECT * FROM dbo.Schnell_accounts a
    WHERE a.user_id=@PID ORDER BY a.name
END
