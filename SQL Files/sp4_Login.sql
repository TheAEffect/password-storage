DROP PROCEDURE IF EXISTS dbo.Schnell_Login;
GO
CREATE PROCEDURE dbo.Schnell_Login
@USERNAME_EMAIL VARCHAR(255)
AS
BEGIN
    SELECT id, password FROM dbo.Schnell_users WHERE username_email=@USERNAME_EMAIL;
END 
