-- noinspection SqlNoDataSourceInspectionForFile
-- noinspection SqlDialectInspectionForFile

create table chat_messages
(
    id         binary(16) default (uuid_to_bin(uuid(), 1)) not null primary key,
    session_id varchar(255)                                not null,
    sender     varchar(50)                                 not null,
    content    text                                        not null,
    created_at timestamp  default CURRENT_TIMESTAMP        not null,
    index idx_chat_messages_session (session_id)
);
