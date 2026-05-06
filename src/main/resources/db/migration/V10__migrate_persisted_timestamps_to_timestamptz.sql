alter table localization_messages
alter
column created_at type timestamptz using created_at at time zone 'UTC',
    alter
column updated_at type timestamptz using updated_at at time zone 'UTC';

alter table users
alter
column last_login_at type timestamptz using last_login_at at time zone 'UTC',
    alter
column created_at type timestamptz using created_at at time zone 'UTC',
    alter
column updated_at type timestamptz using updated_at at time zone 'UTC';

alter table user_roles
alter
column granted_at type timestamptz using granted_at at time zone 'UTC';

alter table audit_logs
alter
column created_at type timestamptz using created_at at time zone 'UTC';
