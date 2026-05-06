alter table user_roles
    add column grant_source varchar(50),
    add column granted_at timestamp,
    add column granted_by_user_id bigint,
    add column reason varchar(255);

update user_roles
set grant_source = 'AUTHENTICATED_LOGIN',
    granted_at   = users.created_at from users
where users.id = user_roles.user_id;

alter table user_roles
    alter column grant_source set not null,
alter
column granted_at set not null;

alter table user_roles
    add constraint fk_user_roles_granted_by_user
        foreign key (granted_by_user_id) references users (id);
