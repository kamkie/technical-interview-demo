alter table audit_logs
    alter column target_id drop not null;

alter table audit_logs
    add column details jsonb not null default '{}'::jsonb;
