-- DATABASE users

CREATE TABLE roles (
                       id INT IDENTITY(1,1) PRIMARY KEY,
                       name NVARCHAR(50) NOT NULL UNIQUE,
                       description NVARCHAR(255) NULL,
                       created_at DATETIMEOFFSET DEFAULT SYSDATETIMEOFFSET() NOT NULL,
                       updated_at DATETIMEOFFSET DEFAULT SYSDATETIMEOFFSET() NOT NULL
);

CREATE TABLE users (
                       id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
                       password_hash NVARCHAR(256) NOT NULL,
                       role_id INT NOT NULL FOREIGN KEY REFERENCES roles(id),
                       created_at DATETIMEOFFSET DEFAULT SYSDATETIMEOFFSET() NOT NULL,
                       updated_at DATETIMEOFFSET DEFAULT SYSDATETIMEOFFSET() NOT NULL
);

INSERT INTO roles (name)
VALUES
    ('ADMIN'),
    ('USER');
