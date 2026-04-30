-- PostgreSQL initialization script for Technical Interview Demo

-- Create the main database user and grant privileges
GRANT ALL PRIVILEGES ON DATABASE technical_interview_demo TO demo_user;

-- Connect to the technical_interview_demo database
\c technical_interview_demo

-- Create schema and grant privileges
GRANT ALL PRIVILEGES ON SCHEMA public TO demo_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO demo_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO demo_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO demo_user;

-- Allow the user to create tables (for Flyway migrations)
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON TABLES TO demo_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON SEQUENCES TO demo_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON FUNCTIONS TO demo_user;

-- Create flyway_schema_history table if it doesn't exist (for Flyway)
CREATE TABLE IF NOT EXISTS flyway_schema_history (
    installed_rank INTEGER NOT NULL,
    version VARCHAR(50),
    description VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    script VARCHAR(1000) NOT NULL,
    checksum INTEGER,
    installed_by VARCHAR(100) NOT NULL,
    installed_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    execution_time INTEGER NOT NULL,
    success BOOLEAN NOT NULL,
    PRIMARY KEY (installed_rank)
);

-- Grant privileges on flyway table
GRANT ALL PRIVILEGES ON flyway_schema_history TO demo_user;

