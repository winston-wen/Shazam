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

drop table if exists hash cascade;
drop sequence if exists hash_id_seq cascade;
create sequence hash_id_seq
  increment 1
  minvalue 1
  maxvalue 2147483647
  start 1
  cache 1;
create table hash (
	hash_id int4 default nextval('hash_id_seq'::regclass) not null,
	f1 int2 not null,
	f2 int2 not null,
	dt int4 not null,
	primary key(hash_id),
	unique (f1, f2, dt)
)
with (OIDS=FALSE);

drop table if exists song_hash cascade;
create table song_hash (
	song_id int4 not null,
	hash_id int4 not null,
	"offset" int4 not null,
	primary key(song_id, hash_id, "offset"),
	foreign key (song_id) references song(song_id),
	foreign key (hash_id) references hash(hash_id)
)
with (OIDS=FALSE);

CREATE INDEX "f1_idx" ON "public"."hash" USING btree ("f1");
CREATE INDEX "f2_idx" ON "public"."hash" USING btree ("f2");
CREATE INDEX "dt_idx" ON "public"."hash" USING btree ("dt");
ALTER TABLE "public"."hash" CLUSTER ON "f1_idx";

CREATE INDEX "song_hash_idx" ON "public"."song_hash" USING btree ("hash_id");
ALTER TABLE "public"."song_hash" CLUSTER ON "song_hash_idx";
