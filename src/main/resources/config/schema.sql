create table if not exists users(
    username varchar(50) not null primary key,
    password varchar(500) not null,
    enabled boolean not null
);

create table if not exists authorities (
    username varchar(50) not null,
    authority varchar(50) not null,
    constraint fk_authorities_users foreign key(username) references users(username)
);

create unique index ix_auth_username on authorities (username, authority);


create table if not exists customers (
    id int not null primary key auto_increment,
    email varchar(255) null,
    password varchar(255) not null,
    cellphone varchar(30) null,
    username varchar(50) not null,
    role varchar(45) not null,

    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp ON UPDATE CURRENT_TIMESTAMP,
    deleted_at timestamp null
);
-- extra
-- ALTER TABLE t1
--     ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;