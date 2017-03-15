drop table if exists users;
drop table if exists updates;
drop table if exists tickets;
drop table if exists units;
drop table if exists assignments;

create table if not exists users (
id int auto_increment primary key not null,
firstname varchar(20) not null, 
lastname varchar(20) not null,
username varchar(20) unique not null,
pass varchar(255) default "",
phone varchar(15) default "",
department varchar(255) default "",
email varchar(255) default "",
position int default 2,
unit_id int default 0
);

create table if not exists updates (
id int auto_increment primary key not null,
ticketId int not null,
modifier varchar(20) not null,
updateDetails TEXT,
modifiedDate DATETIME not null
);

create table if not exists assignments (
ticketId int not null,
technicianUser varchar(20) not null
);

create table if not exists tickets (
id int auto_increment primary key not null,
username varchar(20) not null,
userFirstName varchar(20) not null, 
userLastName varchar(20) not null,
phone varchar(15) not null,
email varchar(50) not null,
department varchar(255) default "",
Progress int default 0,
priority int default 0,
unitId int not null,
subject TEXT not null,
details TEXT not null,
startDate DATETIME,
endDate DATETIME,
lastUpdated DATETIME,
ticketLocation varchar(50) not null
);

create table if not exists units(
id int auto_increment primary key not null,
unitName varchar(255) not null,
phone varchar(15) default "",
location varchar(255) default "",
email varchar(50) default "",
description TEXT
);
-- PASS: abcd
insert into users (id, firstname, lastname, username, pass, phone, email, position, unit_id) values(1, "John", "Smith", "jsmith", "88d4266fd4e6338d13b845fcf289579d209c897823b9217da3e161936f031589", "(555) 555-5555", "jsmith.techit@gmail.com",1, 1);
-- PASS: abcd
insert into users (firstname, lastname, username, pass, phone, email, position, unit_id) values("Jimmy", "Jim", "jjim", "88d4266fd4e6338d13b845fcf289579d209c897823b9217da3e161936f031589", "(555) 555-5555", "jjim.techit@gmail.com",2, 1);
-- PASS: abcd
insert into users (firstname, lastname, username, pass, phone, email, position, unit_id) values("Bob", "Lee", "blee", "88d4266fd4e6338d13b845fcf289579d209c897823b9217da3e161936f031589","(555) 555-5555", "blee.techit@gmail.com", 2, 1);
-- PASS: ohmygod
insert into users (firstname, lastname, username, pass, phone, email, position, unit_id) values("Joseph", "Joestar", "jojo", "5ad0000a66aac644685638066d6c1beabd796dc33a5a827ce7121a94aa78552d","(555) 555-5555", "jojo@test.com", 0, 0); 
-- PASS: hello
insert into users (firstname, lastname, username, pass, phone, email, position, unit_id) values("Brandon", "Ung", "bung@calstatela.edu", "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", "(626) 202-6423", "bung@calstatela.edu", 3, 0);
-- PASS: abcd
insert into users (firstname, lastname, username, pass, phone, email, position, unit_id) values("Joe", "Jo", "jjo", "88d4266fd4e6338d13b845fcf289579d209c897823b9217da3e161936f031589","(555) 555-5555", "jjo@test.com", 2, 1);

insert into tickets (id, username, userFirstName, userLastName, phone, email, Progress, unitId, subject, details, startDate, lastUpdated, ticketLocation) 
values (1, "bung@calstatela.edu", "Brandon", "Ung", "(626) 202-6423", "bung@calstatela.edu", 0, 1, "Projector malfunction.", "The projector is broken in room A220.", "2016-10-13 00:00:01", "2016-10-13 00:00:01", "Engineering and Technology: A220");
insert into tickets (username, userFirstName, userLastName, phone, email, Progress, unitId, subject, details, startDate, lastUpdated, ticketLocation) 
values ("bung@calstatela.edu", "Brandon", "Ung", "(626) 202-6423", "bung@calstatela.edu", 0, 2, "Computer Malfunction", "A computer broke and will not turn on.", "2016-10-13 03:14:07", "2016-10-13 00:00:01", "Engineering and Technology: B9");

insert into units (id, unitName, phone, location, email, description) values (1, "TechOPs", "(555) 555-5555", "Hydrogen Station", "techops@test.com","TechOP, or Technical Operations, is a unit that belongs to ECST. This unit's 
jurisdiction and responsibility falls under the area of the Engineering department. Some examples of their services includes: replacing and creating parts for the ECST's senior design, running the hydrogen station, and other technological
 related issues within the department.");