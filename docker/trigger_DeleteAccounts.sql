DROP TRIGGER IF EXISTS DeleteAccounts
GO
CREATE TRIGGER DeleteAccounts
    ON users
    FOR DELETE
    AS
    DELETE FROM accounts
    WHERE user_id IN(SELECT deleted.id FROM deleted)
GO