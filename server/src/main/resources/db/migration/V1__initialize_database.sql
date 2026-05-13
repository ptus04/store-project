create table carousel
(
    id              binary(16) default (uuid_to_bin(uuid(), 1)) not null primary key,
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
    id         binary(16) default (uuid_to_bin(uuid(), 1)) not null primary key,
    name       varchar(64)                                 not null,
    created_at timestamp  default CURRENT_TIMESTAMP        not null,
    updated_at timestamp  default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP
);

create table products
(
    id                binary(16)     default (uuid_to_bin(uuid(), 1)) not null primary key,
    name              varchar(255)                                    not null,
    description       text                                            null,
    care_instructions text                                            null,
    price             decimal(18, 2) default 0.00                     not null,
    in_stock          int            default 0                        not null,
    is_new            boolean        default true                     not null,
    discount          float          default 0                        not null,
    created_at        timestamp      default CURRENT_TIMESTAMP        not null,
    updated_at        timestamp      default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP,
    deleted_at        timestamp                                       null
);

create table category_product
(
    category_id binary(16) not null,
    product_id  binary(16) not null,
    primary key (category_id, product_id),
    constraint category_product_categories_id_fk foreign key (category_id) references categories (id) on delete cascade,
    constraint category_product_products_id_fk foreign key (product_id) references products (id) on delete cascade
);

create table product_images
(
    id         binary(16) default (uuid_to_bin(uuid(), 1)) not null primary key,
    product_id binary(16)                                  not null,
    file       varchar(128)                                not null,
    created_at timestamp  default CURRENT_TIMESTAMP        not null,
    constraint product_images_products_id_fk foreign key (product_id) references products (id) on delete cascade
);

create table product_sizes
(
    id         binary(16) default (uuid_to_bin(uuid(), 1)) not null primary key,
    product_id binary(16)                                  not null,
    name       varchar(4)                                  not null,
    in_stock   int        default 0                        not null,
    created_at timestamp  default CURRENT_TIMESTAMP        not null,
    updated_at timestamp  default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP,
    constraint product_sizes_products_id_fk foreign key (product_id) references products (id) on delete cascade
);

create table users
(
    id                binary(16) default (uuid_to_bin(uuid(), 1)) not null primary key,
    name              varchar(128)                                not null,
    phone             varchar(10)                                 not null,
    email             varchar(64)                                 null,
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
    updated_at        timestamp  default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP,
    constraint users_pk_2 unique (phone),
    constraint users_pk_3 unique (email)
);

create table orders
(
    id                  binary(16)     default (uuid_to_bin(uuid(), 1)) not null primary key,
    user_id             binary(16)                                      not null,
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
    updated_at          timestamp      default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP,
    constraint orders_users_id_fk foreign key (user_id) references users (id)
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
    constraint order_details_orders_id_fk foreign key (order_id) references orders (id),
    constraint order_details_product_sizes_id_fk foreign key (product_size_id) references product_sizes (id),
    constraint order_details_products_id_fk foreign key (product_id) references products (id)
);

create table order_shipping_addresses
(
    order_id binary(16)   not null primary key,
    name     varchar(128) not null,
    phone    varchar(10)  not null,
    city     varchar(32)  not null,
    district varchar(32)  not null,
    ward     varchar(128) not null,
    address  varchar(128) not null,
    constraint order_shipping_addresses_orders_id_fk foreign key (order_id) references orders (id)
);

create table user_addresses
(
    id         binary(16) default (uuid_to_bin(uuid(), 1)) not null primary key,
    user_id    binary(16)                                  not null,
    city       varchar(32)                                 not null,
    district   varchar(32)                                 null,
    ward       varchar(128)                                not null,
    address    varchar(128)                                not null,
    created_at timestamp  default CURRENT_TIMESTAMP        not null,
    updated_at timestamp  default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP,
    constraint user_addresses_users_id_fk foreign key (user_id) references users (id) on delete cascade
);

create table transactions
(
    id                  binary(16) default (uuid_to_bin(uuid(), 1)) not null primary key,
    order_id            binary(16)                                  null,
    gateway_name        varchar(100)                                not null,
    transaction_code    varchar(255)                                null,
    amount              decimal(18, 2)                              not null,
    transaction_content text                                        null,
    transaction_date    datetime                                    not null,
    raw_payload         json                                        null,
    created_at          timestamp  default CURRENT_TIMESTAMP        not null,
    constraint transactions_orders_id_fk foreign key (order_id) references orders (id),
    index idx_transaction_code (transaction_code)
);
