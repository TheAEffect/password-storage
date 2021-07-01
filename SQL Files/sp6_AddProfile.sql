DROP PROCEDURE IF EXISTS dbo.Schnell_AddProfile;
GO
CREATE PROCEDURE dbo.Schnell_AddProfile
@EMAIL VARCHAR(255),
@PASSWORD VARCHAR(255)
AS
BEGIN
    INSERT INTO dbo.Schnell_users (username_email, password)
    VALUES (@EMAIL ,@PASSWORD)
END
