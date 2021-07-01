DROP PROCEDURE IF EXISTS dbo.Schnell_DeleteProfile;
GO
CREATE PROCEDURE dbo.Schnell_DeleteProfile
(@PID INT)
AS
BEGIN
    DELETE FROM dbo.Schnell_users
    WHERE id=@PID;
END