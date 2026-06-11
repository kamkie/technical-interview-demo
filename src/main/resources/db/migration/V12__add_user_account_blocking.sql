alter table users
    add column blocked_at timestamptz,
    add column blocked_reason varchar(255),
    add column blocked_by_user_id bigint;

alter table users
    add constraint fk_users_blocked_by_user
        foreign key (blocked_by_user_id) references users (id);
