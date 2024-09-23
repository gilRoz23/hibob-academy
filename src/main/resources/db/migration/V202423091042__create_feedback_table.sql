CREATE TABLE IF NOT EXISTS feedback
(
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    feedback TEXT NOT NULL,
    is_anonymous BOOLEAN NOT NULL,
    status BOOLEAN NOT NULL,
    feedback_provider_id BIGINT,
    department VARCHAR(255),
    time_of_submitting TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_feedback_company_id ON feedback (company_id);
CREATE INDEX idx_feedback_department ON feedback (department);
CREATE INDEX idx_feedback_time ON feedback (time_of_submitting);