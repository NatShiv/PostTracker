--liquibase formatted sql

--changeset ShilovaNatalia:tables

create table post_office
(
    index   integer not null
        primary key,
    address varchar(255),
    phone   varchar(255),
    title   varchar(255)
);

create table postman
(
    id                varchar(50) not null
        primary key,
    name              varchar(255),
    post_office_index integer
        constraint fk_post_office_index
            references post_office on delete cascade on update cascade
);

create table recipient
(
    phone   bigint not null
        primary key,
    address varchar(255),
    name    varchar(255)
);

create table postal_item
(
    tracker                varchar(255) not null
        primary key,
    status                 smallint,
    type                   smallint,
    last_post_office_index integer
        constraint fk_last_post_office_index
            references post_office,
    recipient_phone        bigint
        constraint fk_recipient_phone
            references recipient on delete cascade on update cascade
);

create table postal_item_route
(
    postal_item_tracker varchar(255) not null
        constraint fk_postal_item_tracker
            references postal_item on delete cascade on update cascade ,
    route               varchar(255)
);


create table users
(
    username varchar(50)  not null primary key,
    password varchar(120) not null,
    enabled  boolean      not null
);

ALTER TABLE postman
    ADD FOREIGN KEY (id) REFERENCES users (username) on delete cascade on update cascade ;


create table authorities
(
    username  varchar(50) not null,
    authority varchar(50) not null,
    FOREIGN KEY (username) REFERENCES users (username) on delete cascade on update cascade
);

--changeset 2 ShilovaNatalia :default user admin

insert into post_office( index, address,  phone,    title)values('000000', 'null','телефон службы поддержки','Офис службы технического обслуживания');
insert into users( username, password, enabled) values('000000admin', '{bcrypt}$2a$10$yQdXvVDTJtnNxpD2vm.97e.xzYCC.8U2JqKGHnLNxADtn4CG.dSi6',true);
insert into authorities( username, authority) values('000000admin', 'ROLE_SUPER_ADMIN');


