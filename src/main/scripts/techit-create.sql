create table units (
    id          int auto_increment primary key,
    unitName    varchar(255) not null,
    phone       varchar(255) default '',
    location    varchar(255) default '',
    email       varchar(255) default '',
    description TEXT
);

insert into units (unitName, description) values ('TechOPs', concat_ws(' ',
    'Technical Operations, or TechOps,  is a unit in the ECST College.'
    'TechOps runs the Hydrogen Station, and provides technical assistance to',
    'the ECST departments such as creating and replacing part for senior',
    'design project.'));

create table users (
    id          int auto_increment primary key,
    firstname   varchar(255) not null, 
    lastname    varchar(255) not null,
    username    varchar(255) unique not null,
    pass        varchar(255) default '',
    phone       varchar(255) default '',
    department  varchar(255) default '',
    email       varchar(255) default '',
    position    int default 3, -- default to regular user (3)
    unit_id     int default 0 -- user does not belong to any unit
);

insert into users (firstname, lastname, username, pass, email, position) values
    ('System', 'Admin', 'techit', sha2('abcd',256), 'techit@localhost.localdomain', 0);

create table tickets (
    id              int auto_increment primary key,
    username        varchar(255) not null,
    userFirstName   varchar(255) not null, 
    userLastName    varchar(255) not null,
    phone           varchar(255) not null,
    email           varchar(255) not null,
    department      varchar(255) default '',
    Progress        int default 0,
    priority        int default 0,
    unitId          int not null,
    subject         TEXT not null,
    details         TEXT not null,
    startDate       DATETIME,
    endDate         DATETIME,
    lastUpdated     DATETIME,
    ticketLocation  varchar(255) not null
);

create table assignments (
    ticketId        int not null,
    technicianUser  varchar(255) not null
);

create table updates (
    id              int auto_increment primary key,
    ticketId        int not null,
    modifier        varchar(20) not null,
    updateDetails   TEXT,
    modifiedDate    DATETIME not null
);
