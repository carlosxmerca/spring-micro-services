-- DATABASE bankaccounts

CREATE TABLE account_types (
                               account_type_id INT PRIMARY KEY IDENTITY(1,1),
                               account_type_name NVARCHAR(50) NOT NULL
);

CREATE TABLE accounts (
                          account_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
                          account_type_id INT NOT NULL,
                          current_balance DECIMAL(18, 2) NOT NULL,
                          created_at DATETIMEOFFSET NOT NULL DEFAULT SYSDATETIMEOFFSET(),
                          updated_at DATETIMEOFFSET NOT NULL DEFAULT SYSDATETIMEOFFSET(),
                          client_id UNIQUEIDENTIFIER NOT NULL,
                          CONSTRAINT fk_account_type FOREIGN KEY (account_type_id) REFERENCES account_types(account_type_id)
);


INSERT INTO account_types (account_type_name) VALUES ('corriente'), ('ahorros');
