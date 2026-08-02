USE storage;
GO
DROP TRIGGER IF EXISTS dbo.DeleteAccounts
GO
CREATE TRIGGER dbo.DeleteAccounts
    ON dbo.users
    FOR DELETE
    AS
    DELETE FROM dbo.accounts
    WHERE user_id IN(SELECT deleted.id FROM deleted)
GO