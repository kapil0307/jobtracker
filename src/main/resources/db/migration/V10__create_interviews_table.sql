CREATE TABLE interviews(
        id BIGSERIAL PRIMARY KEY ,
        job_application_id BIGINT not null ,
        type VARCHAR(50) not null ,
        status VARCHAR(50) not null ,
        scheduled_at TIMESTAMP not null ,
        meeting_link VARCHAR(500) not null ,
        notes VARCHAR(1000),
        feedback VARCHAR(1000),
        rating INTEGER,

        CONSTRAINT fk_interviews_job_application
                FOREIGN KEY (job_application_id)
                REFERENCES job_applications(id)
                ON DELETE CASCADE,

        CONSTRAINT chk_interview_rating
                CHECK ( rating IS NULL OR (rating BETWEEN 1 AND 5))
);