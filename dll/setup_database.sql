-- Tables not created by hibernate after spring.jpa.hibernate.ddl-auto=none was set 2023-12-11

create table last_read_feed_item (
	id int(11) not null,
	feed_item_id UUID not null,
	primary key (id)
);
