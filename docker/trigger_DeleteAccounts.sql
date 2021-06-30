DROP TRIGGER IF EXISTS dbo.Schnell_DeleteAccounts
GO
CREATE TRIGGER dbo.Schnell_DeleteAccounts
    ON dbo.Schnell_users
    FOR DELETE
    AS
    DELETE FROM dbo.Schnell_accounts
    WHERE user_id IN(SELECT deleted.id FROM deleted)
GO