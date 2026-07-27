CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE users (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        email VARCHAR(150) NOT NULL UNIQUE,
                        password VARCHAR(254) NOT NULL ,
                        enabled BOOLEAN NOT NULL DEFAULT TRUE,
                        created_at TIMESTAMP NOT NULL ,
                        updated_at TIMESTAMP NOT NULL
);

CREATE TABLE user_roles(
                        user_id BIGINT NOT NULL ,
                        role_id BIGINT NOT NULL ,

                        FOREIGN KEY (user_id) REFERENCES users(id),
                        FOREIGN KEY (role_id) REFERENCES roles(id),

                        UNIQUE (user_id, role_id)
);
