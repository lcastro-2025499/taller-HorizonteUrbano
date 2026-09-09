drop database if exists HorizonteUrbano_in4am;
create database HorizonteUrbano_in4am;
use HorizonteUrbano_in4am; 

create table Role(
	id_role int primary key,
    name_role varchar(40) not null,
    active boolean not null default true 
);

create table PropertyType(
	id_property_type int not null primary key,
    type_name varchar(20) not null,
    active boolean not null default true  
);

create table State(
	id_state int not null primary key,
    state_name varchar(20) not null,
    active boolean not null default true 
);

create table Users( 
	id_user varchar(36) not null primary key,
	name varchar(40) not null ,
    last_name varchar(40) not null,
    password varchar(80) not null,
    email varchar(40) unique not null,
    user_name varchar(30) unique not null,
    active boolean not null default true,
    id_role int not null, foreign key (id_role)
		references Role(id_role) on delete restrict 
		on update cascade
);

create table Properties(
	 id_property int not null primary key,
     internal_code varchar(20) unique not null,
     address varchar(35) not null,
     area_m2 float not null check (area_m2 > 0),
     price decimal(10,2) not null check (price > 0),
     active boolean not null default true,
     date_register datetime default current_timestamp not null,
     update_date datetime not null default
		current_timestamp on update current_timestamp,
     image_url varchar(255) not null,
     id_state int not null,foreign key(id_state) 
		references State(id_state) on delete restrict 
		on update cascade,
     id_user varchar(36) not null,foreign key(id_user) 
		references Users(id_user) on delete restrict 
		on update cascade,
     id_property_type int not null,foreign key(id_property_type) 
		references PropertyType(id_property_type) on delete restrict 
		on update cascade
);

create table PropertyImages (
    id_image int auto_increment primary key,
    id_property int not null, foreign key (id_property) 
        references Properties(id_property) on delete cascade 
        on update cascade,
    image_url varchar(255) not null
);

-----------------------------------------------------------------
# CREATE USER
delimiter $$
create procedure sp_create_user(in name_p varchar(40),
								in last_name_p varchar(40),
								in password_p varchar(80),
								in email_p varchar(40),
								in user_name_p varchar(30),
								in active_p boolean,
								in id_role_p int)
begin
    insert into Users (id_user, name, last_name, password, email, user_name, active, id_role)
    values (uuid(), name_p, last_name_p, password_p, email_p, user_name_p, active_p, id_role_p);
end $$
delimiter ;

# READ USER
delimiter $$
create procedure sp_read_users()
begin
    select id_user, name, last_name, email, user_name, active, id_role
    from Users
    where active = true;
end $$
delimiter ;

# READ USER
delimiter $$
create procedure sp_read_userid(in id_user_p varchar(36))
begin
    select id_user, name, last_name, email, user_name, active, id_role
    from Users
    where id_user = id_user_p;
end $$
delimiter ;

# UPDATE USER
delimiter $$
create procedure sp_update_user(in id_user_p varchar(36),
								in name_p varchar(40),
								in last_name_p varchar(40),
								in password_p varchar(80),
								in email_p varchar(40),
								in user_name_p varchar(30),
								in active_p boolean,
								in id_role_p int)
begin
    update Users
    set
        name = name_p,
        last_name = last_name_p,
        password = password_p,
        email = email_p,
        user_name = user_name_p,
        active = active_p,
        id_role = id_role_p
    where id_user = id_user_p;
end $$
delimiter ;

# DELETE USER
delimiter $$
create procedure sp_delete_user(in id_user_p varchar(36))
begin
    update Users set active = false where id_user = id_user_p;
end $$
delimiter ;

# LOGIN USER
delimiter $$
create procedure sp_login_user(
    in identifier_p varchar(40),
    in password_p varchar(80))
begin
    select id_user, name, last_name, email, user_name, active, id_role
    from Users
    where (email = identifier_p or user_name = identifier_p)
      and password = password_p
      and active = true;
end $$
delimiter ;

-----------------------------------------------------------------
# CREATE PROPERTY
delimiter $$
create procedure sp_create_property(in id_property_p int,
									in internal_code_p varchar(20),
									in address_p varchar(35),
									in area_m2_p float,
									in price_p decimal(10,2),
									in active_p boolean,
									in date_register_p datetime,
									in update_date_p datetime,
									in image_url_p varchar(255),
									in id_state_p int,
									in id_user_p varchar(36),
									in id_property_type_p int)
begin
    insert into Properties (
        id_property, internal_code, address, area_m2, price, active,
        date_register, update_date, image_url, id_state, id_user, id_property_type)
        values (
        id_property_p, internal_code_p, address_p, area_m2_p, price_p, active_p,
        date_register_p, update_date_p, image_url_p, id_state_p, id_user_p, id_property_type_p);
end $$
delimiter ;

# READ PROPERTY
delimiter $$
create procedure sp_read_properties()
begin
    select * from Properties where active = true;
end $$
delimiter ;

# READ PROPERTY
delimiter $$
create procedure sp_read_propertyid(in id_property_p int)
begin
    select * from Properties where id_property = id_property_p;
end $$
delimiter ;

# UPDATE PROPERTY
delimiter $$
create procedure sp_update_property(in id_property_p int,
									in internal_code_p varchar(20),
									in address_p varchar(35),
									in area_m2_p float,
									in price_p decimal(10,2),
									in active_p boolean,
									in date_register_p datetime,
									in update_date_p datetime,
									in image_url_p varchar(255),
									in id_state_p int,
									in id_user_p varchar(36),
									in id_property_type_p int)
begin
    update Properties
    set
        internal_code = internal_code_p,
        address = address_p,
        area_m2 = area_m2_p,
        price = price_p,
        active = active_p,
        date_register = date_register_p,
        update_date = update_date_p,
        image_url = image_url_p,
        id_state = id_state_p,
        id_user = id_user_p,
        id_property_type = id_property_type_p
    where id_property = id_property_p;
end $$
delimiter ;

# DELETE PROPERTY
delimiter $$
create procedure sp_delete_property(in id_property_p int)
begin
    update Properties set active = false where id_property = id_property_p;
end $$
delimiter ;

-----------------------------------------------------------------
# CREATE ROLE
delimiter $$
create procedure sp_create_role(in id_role_p int, in name_role_p varchar(40))
begin
    insert into Role (id_role, name_role) values (id_role_p, name_role_p);
end $$
delimiter ;

# LEER ROLE
delimiter $$
create procedure sp_read_roles()
begin
    select * from Role where active = true;
end $$
delimiter ;

# UPDATE ROLE
delimiter $$
create procedure sp_update_role(in id_role_p int, in name_role_p varchar(40))
begin
    update Role set name_role = name_role_p where id_role = id_role_p;
end $$
delimiter ;

# DELETE ROLE
delimiter $$
create procedure sp_delete_role(in id_role_p int)
begin
    update Role set active = false where id_role = id_role_p;
end $$
delimiter ;

-----------------------------------------------------------------
# CREATE PROPERTY TYPE
delimiter $$
create procedure sp_create_propertytype(in id_property_type_p int, in type_name_p varchar(20))
begin
    insert into PropertyType (id_property_type, type_name) values (id_property_type_p, type_name_p);
end $$
delimiter ;

# READ PROPERTY TYPE
delimiter $$
create procedure sp_read_propertytypes()
begin
    select * from PropertyType where active = true;
end $$
delimiter ;

# UPDATE PROPERTY TYPE
delimiter $$
create procedure sp_update_propertytype(in id_property_type_p int, in type_name_p varchar(20))
begin
    update PropertyType set type_name = type_name_p where id_property_type = id_property_type_p;
end $$
delimiter ;

# DELETE PROPERTY TYPE
delimiter $$
create procedure sp_delete_propertytype(in id_property_type_p int)
begin
    update PropertyType set active = false where id_property_type = id_property_type_p;
end $$
delimiter ;

-----------------------------------------------------------------
# CREATE STATE
delimiter $$
create procedure sp_create_state(in id_state_p int, in state_name_p varchar(20))
begin
    insert into State (id_state, state_name) values (id_state_p, state_name_p);
end $$
delimiter ;

# READ STATE
delimiter $$
create procedure sp_read_states()
begin
    select * from State where active = true;
end $$
delimiter ;

# UPDATE STATE
delimiter $$
create procedure sp_update_state(in id_state_p int, in state_name_p varchar(20))
begin
    update State set state_name = state_name_p where id_state = id_state_p;
end $$
delimiter ;

# DELETE STATE
delimiter $$
create procedure sp_delete_state(in id_state_p int)
begin
    update State set active = false where id_state = id_state_p;
end $$
delimiter ;