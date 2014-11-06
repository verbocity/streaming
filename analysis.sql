use streaming;

drop table if exists temptran;
drop table if exists transactions;
drop table if exists cities;
drop table if exists halfcartesian;
drop table if exists distances;

create table temptran (
	iban varchar(40), 
    description varchar(30), 
    city varchar(25),
    country varchar(2), 
    dt long, 
    amount float, 
    statuscode text);

load data infile 'C:/Users/Steven/IdeaProjects/streaming/PX141013.csv' 
into table temptran
fields terminated by ';'
enclosed by '"'
lines terminated by '\r\n';

create table transactions
select 
	iban, 
    description, 
    city, 
    country, 
    dt as dtlong, 
    FROM_UNIXTIME(dt / 1000) as dt, 
    amount, 
    statuscode
from temptran;

create table postcode (
	id int,
    postcode varchar(6),
    postcode_id int,
    pnum int,
    pchar varchar(2),
    minnumber int,
    maxnumber int,
    numbertype varchar(10),
    street varchar(20),
    city varchar(20),
    city_id int,
    municipality varchar(20),
    municipality_id int,
    province varchar(20),
    province_code varchar(2),
    lat float,
    lon float,
    rd_x float,
    rd_y float,
    location_detail varchar(10),
    changed_date datetime
);

load data infile 'C:/Users/Steven/Downloads/postcode_NL_en.csv' 
into table postcode
fields terminated by ','
enclosed by '"'
lines terminated by '\r\n';

create table cities as 
	select 
		city, 
		municipality, 
		avg(lat) as lat, 
        avg(lon) as lon
    from postcode
    where location_detail = 'pnum'
    group by city;

create table halfcartesian as
	select 
		c1.city as city1, 
		c2.city as city2,
		c1.lat as latc1,
        c1.lon as lonc1,
		c2.lat as latc2,
        c2.lon as lonc2
	from cities c1, cities c2
	where c1.city < c2.city;

create table distances as
	select 
		city1,
		city2,
		6378 * acos(cos(latc1 * 0.01745329251) 
			 * cos(latc2 * 0.01745329251) 
			 * cos((lonc2 * 0.01745329251) - (lonc1 * 0.01745329251)) 
			 + sin(latc1 * 0.01745329251) 
			 * sin(latc2 * 0.01745329251)) as distance
	from halfcartesian;
    
select * 
from distances 
order by distance desc
limit 1000;