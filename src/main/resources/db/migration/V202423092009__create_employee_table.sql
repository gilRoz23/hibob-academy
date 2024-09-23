-- Create employee table
CREATE TABLE employee (
                           id BIGSERIAL PRIMARY KEY,
                           first_name VARCHAR(255) NOT NULL,
                           last_name VARCHAR(255) NOT NULL,
                           role VARCHAR(50) NOT NULL,
                           company_id BIGINT REFERENCES company(id)
);