CREATE UNIQUE INDEX uk_job_application_owner_company_title
    ON job_applications (
                         owner_id,
                         company_id,
                         LOWER(job_title)
        );