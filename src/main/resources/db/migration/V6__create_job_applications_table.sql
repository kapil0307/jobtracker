CREATE TABLE job_applications(
    id BIGSERIAL PRIMARY KEY ,
    job_title VARCHAR(150) NOT NULL ,
    status VARCHAR(30) NOT NULL ,
    applied_date DATE,
    job_url VARCHAR(500),
    location VARCHAR(150),
    salary_range VARCHAR(100),
    notes TEXT,
    company_id BIGINT not null ,
    owner_id BIGINT not null ,
    created_at TIMESTAMP not null ,
    updated_at TIMESTAMP not null ,

    CONSTRAINT fk_job_applications_company
          FOREIGN KEY (company_id)
          REFERENCES companies(id),
    CONSTRAINT fk_job_application_owner
          FOREIGN KEY (owner_id)
          REFERENCES users(id)
);