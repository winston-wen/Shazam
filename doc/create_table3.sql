drop table if exists song cascade;
drop sequence if exists song_id_seq CASCADE;
create sequence song_id_seq
increment 1
minvalue 1
maxvalue 2147483647
start 1
cache 1;
create table song (
  song_id int4 default nextval('song_id_seq'::regclass) not null,
  name text unique not null,
  primary key(song_id)
)
with (OIDS=FALSE);

drop table if exists song_hash cascade;
create table song_hash (
  hash_id int4,
  song_id int4,
  "offset"  int4,
  primary key (hash_id, song_id, "offset"),
  foreign key (song_id) references song (song_id)
)
with (OIDS = FALSE);

create index "hash_id_idx" on "song_hash" using btree("hash_id");
alter table "song_hash" cluster on "hash_id_idx";

