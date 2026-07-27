CREATE TABLE companies(
            id BIGSERIAL PRIMARY KEY,
            name VARCHAR(150) NOT NULL,
            website VARCHAR(255),
            location VARCHAR(150),
            industry VARCHAR(100),
            notes TEXT,
            owner_id BIGINT NOT NULL ,
            created_at TIMESTAMP NOT NULL ,
            updated_at TIMESTAMP NOT NULL ,

            CONSTRAINT fk_companies_owner
                      FOREIGN KEY (owner_id)
                      REFERENCES users(id)
);