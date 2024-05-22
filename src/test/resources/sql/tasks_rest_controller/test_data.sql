insert into application_user(id, username, password) values
('4d8d4209-0553-45b7-a088-4979449519cc', 'user1','{noop}password1'),
('006a1896-58a4-4f35-916e-59c706df82c9', 'user2','{noop}password2');

insert into task(id, details, completed, id_application_user)
values ('3d63983b-117c-4139-8930-2fec84579360', 'first task', false,'4d8d4209-0553-45b7-a088-4979449519cc'),
        ('3d63983b-117c-4139-8930-2fec84579361', 'second task', true,'4d8d4209-0553-45b7-a088-4979449519cc'),
        ('586f4625-1722-4a1a-9895-365129720f57', 'third task', true, '006a1896-58a4-4f35-916e-59c706df82c9');