-- DATABASE account-transactions

CREATE TABLE transaction_type (
                                  type_id INT PRIMARY KEY IDENTITY(1,1),
                                  type_name NVARCHAR(50) NOT NULL
);

CREATE TABLE account_transaction (
                                     transaction_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
                                     origin_account_id UNIQUEIDENTIFIER NOT NULL,
                                     destination_account_id UNIQUEIDENTIFIER NULL,
                                     amount DECIMAL(18,2) NOT NULL,
                                     transaction_type_id INT NOT NULL,
                                     created_at DATETIMEOFFSET NOT NULL DEFAULT SYSDATETIMEOFFSET(),
                                     CONSTRAINT fk_account_transaction_type FOREIGN KEY (transaction_type_id) REFERENCES transaction_type(type_id)
);

INSERT INTO transaction_type (type_name) VALUES
                                             ('Abono'),
                                             ('Retiro'),
                                             ('Transferencia');
