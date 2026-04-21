create table carousel
(
    id              binary(16) default (uuid_to_bin(uuid(), 1)) not null
        primary key,
    title           varchar(32)                                 not null,
    content         varchar(64)                                 not null,
    link            varchar(128)                                not null,
    landscape_image varchar(128)                                not null,
    portrait_image  varchar(128)                                null,
    created_at      timestamp  default CURRENT_TIMESTAMP        not null,
    updated_at      timestamp  default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP
);

create table categories
(
    id         binary(16) default (uuid_to_bin(uuid(), 1)) not null
        primary key,
    name       varchar(64)                                 not null,
    created_at timestamp  default (now())                  not null,
    updated_at timestamp  default (now())                  not null on update CURRENT_TIMESTAMP
);

create table products
(
    id                binary(16)     default (uuid_to_bin(uuid(), 1)) not null
        primary key,
    name              varchar(255)                                    not null,
    description       text                                            null,
    care_instructions text                                            null,
    price             decimal(18, 2) default 0.00                     not null,
    in_stock          int            default 0                        not null,
    is_new            tinyint(1)     default 0                        not null,
    discount          float          default 0                        not null,
    created_at        timestamp      default (now())                  not null,
    updated_at        timestamp      default (now())                  not null on update CURRENT_TIMESTAMP
);

create table category_product
(
    category_id binary(16) not null,
    product_id  binary(16) not null,
    primary key (category_id, product_id),
    constraint category_product_categories_id_fk
        foreign key (category_id) references categories (id)
            on delete cascade,
    constraint category_product_products_id_fk
        foreign key (product_id) references products (id)
            on delete cascade
);

create table product_images
(
    id         binary(16) default (uuid_to_bin(uuid(), 1)) not null
        primary key,
    product_id binary(16)                                  not null,
    file       varchar(128)                                not null,
    created_at timestamp  default CURRENT_TIMESTAMP        not null,
    constraint product_images_products_id_fk
        foreign key (product_id) references products (id)
            on delete cascade
);

create table product_sizes
(
    id         binary(16) default (uuid_to_bin(uuid(), 1)) not null
        primary key,
    product_id binary(16)                                  not null,
    name       varchar(4)                                  not null,
    created_at timestamp  default CURRENT_TIMESTAMP        not null,
    updated_at timestamp  default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP,
    constraint product_sizes_pk_2
        unique (name),
    constraint product_sizes_products_id_fk
        foreign key (product_id) references products (id)
            on delete cascade
);

create table users
(
    id         binary(16) default (uuid_to_bin(uuid(), 1)) not null
        primary key,
    name       varchar(128)                                not null,
    phone      varchar(10)                                 not null,
    email      varchar(64)                                 null,
    password   varchar(255)                                not null comment 'Hashed password',
    role       tinyint    default 0                        not null comment '0 = CUSTOMER;1 = EMPLOYEE;2 = ADMIN',
    gender     tinyint(1)                                  null,
    birth_date date                                        null,
    created_at timestamp  default CURRENT_TIMESTAMP        not null,
    updated_at timestamp  default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP,
    constraint users_pk_2
        unique (phone),
    constraint users_pk_3
        unique (email)
);

create table orders
(
    id                 binary(16) default (uuid_to_bin(uuid(), 1)) not null
        primary key,
    user_id            binary(16)                                  not null,
    order_date         datetime   default CURRENT_TIMESTAMP        not null,
    shipping_date      datetime                                    not null comment 'Expected shipping date',
    payment_method     tinyint    default 0                        not null comment '0 = Online Payment',
    status             tinyint    default 0                        not null comment '0 = unpaid;1 = paid;2 = packaging;3 = shipping;4 = completed;5 = cancelled',
    total              decimal(18, 2)                              not null,
    note               tinytext                                    null,
    cancelation_reason tinytext                                    null,
    created_at         timestamp  default CURRENT_TIMESTAMP        not null,
    updated_at         timestamp  default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP,
    constraint orders_users_id_fk
        foreign key (user_id) references users (id)
);

create table order_details
(
    order_id        binary(16)     not null,
    product_id      binary(16)     not null,
    product_size_id binary(16)     null,
    quantity        int default 1  not null,
    price           decimal(18, 2) not null,
    subtotal        decimal(18, 2) not null,
    primary key (order_id, product_id),
    constraint order_details_orders_id_fk
        foreign key (order_id) references orders (id),
    constraint order_details_product_sizes_id_fk
        foreign key (product_size_id) references product_sizes (id),
    constraint order_details_products_id_fk
        foreign key (product_id) references products (id)
);

create table order_shipping_addresses
(
    order_id binary(16)   not null
        primary key,
    name     varchar(128) not null,
    phone    varchar(10)  not null,
    city     varchar(32)  not null,
    district varchar(32)  not null,
    ward     varchar(128) not null,
    address  varchar(128) not null,
    constraint order_shipping_addresses_orders_id_fk
        foreign key (order_id) references orders (id)
);

create table user_addresses
(
    id         binary(16) default (uuid_to_bin(uuid(), 1)) not null
        primary key,
    user_id    binary(16)                                  not null,
    city       varchar(32)                                 not null,
    district   varchar(32)                                 null,
    ward       varchar(128)                                not null,
    address    varchar(128)                                not null,
    created_at timestamp  default CURRENT_TIMESTAMP        not null,
    updated_at timestamp  default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP,
    constraint user_addresses_users_id_fk
        foreign key (user_id) references users (id)
            on delete cascade
);


