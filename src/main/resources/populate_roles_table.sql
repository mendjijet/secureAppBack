select * from roles;

insert into roles(name, permission) VALUES ('ROLE_USER', 'READ:USER,READ:CUSTOMER'),
                                           ('ROLE_MANAGER', 'READ:USER,READ:CUSTOMER,UPDATE:USER,UPDATE:CUSTOMER'),
                                           ('ROLE_ADMIN', 'READ:USER,READ:CUSTOMER,CREATE:USER,CREATE:CUSTOMER,UPDATE:USER,UPDATE:CUSTOMER'),
                                           ('ROLE_SYSADMIN', 'READ:USER,READ:CUSTOMER,CREATE:USER,CREATE:CUSTOMER,UPDATE:USER,UPDATE:CUSTOMER,DELETE:USER,DELETE:CUSTOMER');

insert into events(type, description) VALUES ('LOGIN_ATTEMPT','You tried to log in'),
                                             ('LOGIN_ATTEMPT_FAILURE','You tried to log in and you failed'),
                                             ('LOGIN_ATTEMPT_SUCCESS','You tried to log in and you succeeded'),
                                             ('PROFILE_UPDATE','You updated your profile information'),
                                             ('PROFILE_PICTURE_UPDATE','You updated your profile picture'),
                                             ('ROLE_UPDATE','You updated your role and permissions'),
                                             ('ACCOUNT_SETTINGS_UPDATE','You updated your account settings'),
                                             ('PASSWORD_UPDATE','You updated your password'),
                                             ('MFA_UPDATE','You updated your MFA settings');

ALTER TABLE public.customer
    ALTER COLUMN image_url SET DEFAULT 'http://localhost:8080/user/image/user.png'::character varying;


