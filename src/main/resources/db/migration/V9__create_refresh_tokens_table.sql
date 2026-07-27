CREATE TABLE refresh_tokens(
    id BIGSERIAL PRIMARY KEY ,
    token VARCHAR(500) NOT NULL ,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL ,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGSERIAL NOT NULL ,

    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        On DELETE CASCADE
);