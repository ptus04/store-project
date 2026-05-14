create table carousel
(
    id              binary(16) default (uuid_to_bin(uuid(), 1)) not null primary key,
    title           varchar(32)                                 not null unique,
    content         varchar(64)                                 not null,
    link            varchar(128)                                not null,
    landscape_image varchar(128)                                not null,
    portrait_image  varchar(128)                                null,
    created_at      timestamp  default CURRENT_TIMESTAMP        not null,
    updated_at      timestamp  default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP
);

create table categories
(
    id         binary(16) default (uuid_to_bin(uuid(), 1)) not null primary key,
    name       varchar(64)                                 not null unique,
    created_at timestamp  default CURRENT_TIMESTAMP        not null,
    updated_at timestamp  default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP
);

create table products
(
    id                binary(16)     default (uuid_to_bin(uuid(), 1))   not null primary key,
    name              varchar(255)                                      not null unique,
    description       text                                              null,
    care_instructions text                                              null,
    price             decimal(18, 2) default 0.00                       not null,
    in_stock          int            default 0                          not null,
    is_new            boolean        default true                       not null,
    discount          float          default 0                          not null,
    price_discount    decimal(18, 2) as (price * (1 - discount)) stored not null,
    created_at        timestamp      default CURRENT_TIMESTAMP          not null,
    updated_at        timestamp      default CURRENT_TIMESTAMP          not null on update CURRENT_TIMESTAMP,
    deleted_at        timestamp                                         null
);

create table category_product
(
    category_id binary(16) not null references categories (id) on delete cascade,
    product_id  binary(16) not null references products (id) on delete cascade,
    primary key (category_id, product_id)
);

create table product_images
(
    id         binary(16) default (uuid_to_bin(uuid(), 1)) not null primary key,
    product_id binary(16)                                  not null references products (id) on delete cascade,
    file       varchar(128)                                not null,
    created_at timestamp  default CURRENT_TIMESTAMP        not null
);

create table product_sizes
(
    id         binary(16) default (uuid_to_bin(uuid(), 1)) not null primary key,
    product_id binary(16)                                  not null references products (id) on delete cascade,
    name       varchar(4)                                  not null,
    in_stock   int        default 0                        not null,
    created_at timestamp  default CURRENT_TIMESTAMP        not null,
    updated_at timestamp  default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP
);

create table users
(
    id                binary(16) default (uuid_to_bin(uuid(), 1)) not null primary key,
    name              varchar(128)                                not null unique,
    phone             varchar(10)                                 not null unique,
    email             varchar(64)                                 null unique,
    password          varchar(255)                                not null,
    role              enum (
        'CUSTOMER',
        'EMPLOYEE',
        'ADMIN')                 default 'CUSTOMER'               not null,
    gender            enum ('MALE', 'FEMALE')                     null,
    birth_date        date                                        null,
    phone_verified_at timestamp                                   null,
    email_verified_at timestamp                                   null,
    created_at        timestamp  default CURRENT_TIMESTAMP        not null,
    updated_at        timestamp  default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP
);

create table carts
(
    id         binary(16) default (uuid_to_bin(uuid(), 1)) primary key,
    user_id    binary(16)                           not null unique references users (id),
    created_at timestamp  default CURRENT_TIMESTAMP not null,
    updated_at timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
);

create table cart_items
(
    id              binary(16) default (uuid_to_bin(uuid(), 1)) primary key,
    cart_id         binary(16)                           not null references carts (id) on delete cascade,
    product_id      binary(16)                           not null references products (id),
    product_size_id binary(16)                           null references product_sizes (id),
    quantity        int                                  not null,
    created_at      timestamp  default CURRENT_TIMESTAMP not null,
    updated_at      timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    unique key (cart_id, product_id, product_size_id)
);

create table orders
(
    id                  binary(16)     default (uuid_to_bin(uuid(), 1)) not null primary key,
    order_code          varchar(20)                                     not null unique,
    user_id             binary(16)                                      not null references users (id),
    order_date          datetime       default CURRENT_TIMESTAMP        not null,
    shipping_date       datetime                                        not null,
    payment_method      enum ('SePay') default 'SePay'                  not null,
    status              enum (
        'UNPAID',
        'PAID',
        'PACKAGING',
        'SHIPPING',
        'COMPLETED',
        'CANCELLED',
        'REFUNDED')                    default 'UNPAID'                 not null,
    total               decimal(18, 2)                                  not null,
    note                tinytext                                        null,
    cancellation_reason tinytext                                        null,
    created_at          timestamp      default CURRENT_TIMESTAMP        not null,
    updated_at          timestamp      default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP
);

create table order_details
(
    id              binary(16) default (uuid_to_bin(uuid(), 1)) not null primary key,
    order_id        binary(16)                                  not null references orders (id),
    product_id      binary(16)                                  not null references products (id),
    product_size_id binary(16)                                  null references product_sizes (id),
    quantity        int        default 1                        not null,
    price           decimal(18, 2)                              not null,
    subtotal        decimal(18, 2)                              not null,
    unique key (order_id, product_id, product_size_id)
);

create table order_shipping_addresses
(
    order_id binary(16)   not null primary key references orders (id),
    name     varchar(128) not null,
    phone    varchar(10)  not null,
    city     varchar(32)  not null,
    district varchar(32)  not null,
    ward     varchar(128) not null,
    address  varchar(128) not null
);

create table user_addresses
(
    id         binary(16) default (uuid_to_bin(uuid(), 1)) not null primary key,
    user_id    binary(16)                                  not null references users (id) on delete cascade,
    city       varchar(32)                                 not null,
    district   varchar(32)                                 null,
    ward       varchar(32)                                 not null,
    address    varchar(64)                                 not null,
    created_at timestamp  default CURRENT_TIMESTAMP        not null,
    updated_at timestamp  default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP
);

create table transactions
(
    id               binary(16) default (uuid_to_bin(uuid(), 1)) not null primary key,
    order_id         binary(16)                                  not null references orders (id),
    transaction_code varchar(14)                                 not null,
    reference_code   varchar(32)                                 not null,
    raw_payload      json                                        not null,
    gateway_name     varchar(32)                                 not null,
    content          text                                        not null,
    amount           decimal(18, 2)                              not null,
    transaction_date datetime                                    not null,
    created_at       timestamp  default CURRENT_TIMESTAMP        not null
);
