USE storage;
GO
DROP PROCEDURE IF EXISTS dbo.DeleteProfile;
GO
CREATE PROCEDURE dbo.DeleteProfile
(@PID INT)
AS
BEGIN
    DELETE FROM dbo.users
    WHERE id=@PID;
END