CREATE TABLE notifications (
                               id BIGSERIAL PRIMARY KEY,

                               user_id BIGINT NOT NULL,
                               interview_id BIGINT NOT NULL,

                               type VARCHAR(30) NOT NULL,
                               title VARCHAR(150) NOT NULL,
                               message VARCHAR(500) NOT NULL,

                               scheduled_for TIMESTAMP NOT NULL,
                               sent_at TIMESTAMP,
                               read_at TIMESTAMP,

                               status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_notification_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_notification_interview
                                   FOREIGN KEY (interview_id)
                                       REFERENCES interviews(id)
                                       ON DELETE CASCADE,

                               CONSTRAINT uk_notification_interview
                                   UNIQUE (interview_id)
);