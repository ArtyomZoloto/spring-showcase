create table application_user
(
    id         uuid primary key,
    username varchar(255) not null,
    password text
);

create unique index idx_application_user_username on application_user (username);
