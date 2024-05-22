alter table task add column
id_application_user uuid not null references application_user(id);

create index idx_task_application_user on task(id_application_user);