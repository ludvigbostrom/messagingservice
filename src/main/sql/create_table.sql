create table messages
(
  msgid     serial      not null
    constraint messages_pkey
    primary key,
  timestamp timestamp   not null,
  sender    varchar(30) not null,
  receiver  varchar(30) not null,
  text      text,
  isread    boolean
);

create unique index messages_msgid_uindex
  on messages (msgid);
