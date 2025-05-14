CREATE TABLE users (
                       user_id VARCHAR(255) NOT NULL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL,
                       name VARCHAR(255),
                       student_id VARCHAR(255) NOT NULL,
                       semester INT,
                       role ENUM('ABLE', 'MASTER', 'PENDING') NOT NULL,
                       created_at BIGINT NOT NULL,
                       deleted_at BIGINT
);

CREATE TABLE images (
                        id VARCHAR(255) NOT NULL PRIMARY KEY,
                        uploader_id VARCHAR(255),
                        filename VARCHAR(255),
                        filepath VARCHAR(255),
                        width INT NOT NULL,
                        height INT NOT NULL,
                        size_in_bytes BIGINT NOT NULL,
                        uploaded_at BIGINT NOT NULL
);