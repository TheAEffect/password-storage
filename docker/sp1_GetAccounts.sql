USE storage;
GO
DROP PROCEDURE IF EXISTS dbo.GetAccounts;
GO
CREATE PROCEDURE dbo.GetAccounts
(@PID INT)
AS
BEGIN
    SELECT * FROM dbo.accounts a
    WHERE a.user_id=@PID ORDER BY a.name
END