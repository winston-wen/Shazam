drop table if exists song cascade;
create table song (
	song_id int4 not null,
	name text unique not null,
	primary key(song_id)
)
with (OIDS=FALSE);

drop table if exists hash cascade;
create table hash (
	hash_id int4 not null,
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

create or replace function song_ins()
returns trigger as
$$
declare
	count record;
begin
	for count in execute 'select count(song_id) as value from song;' loop
		if count.value < 1 then 
			new.song_id:=0;
		else 
			new.song_id:=(select max(song.song_id)+1 from song);
		end if;
	end loop;
	return new;
end
$$
language plpgsql;

drop trigger if exists song_pk_auto_inc on song;
create trigger song_pk_auto_inc before insert on song for each row
	execute procedure song_ins();

create or replace function hash_ins()
returns trigger as
$$
declare
	count record;
begin
	for count in execute 'select count(hash_id) as value from hash;' loop
		if count.value < 1 then 
			new.hash_id:=0;
		else 
			new.hash_id:=(select max(hash.hash_id)+1 from hash);
		end if;
	end loop;
	return new;
end
$$
language plpgsql;

drop trigger if exists hash_pk_auto_inc on hash;
create trigger hash_pk_auto_inc before insert on hash for each row
	execute procedure hash_ins();