drop database if exists HorizonteUrbano_in4am;
create database HorizonteUrbano_in4am;
use HorizonteUrbano_in4am;

create table Role(
    id_role int not null,
    name_role varchar(40) not null,
    active boolean not null default true,
    constraint pk_role primary key (id_role)
);

create table PropertyType(
    id_property_type int not null,
    type_name varchar(20) not null,
    active boolean not null default true,
    constraint pk_property_type primary key (id_property_type)
);

create table State(
    id_state int not null,
    state_name varchar(20) not null,
    active boolean not null default true,
    constraint pk_state primary key (id_state)
);

create table Users(
    id_user varchar(36) not null,
    name varchar(40) not null,
    last_name varchar(40) not null,
    password varchar(80) not null,
    email varchar(40) not null,
    user_name varchar(30) not null,
    active boolean not null default true,
    id_role int not null,
    constraint pk_users primary key (id_user),
    constraint uk_users_email unique (email),
    constraint uk_users_user_name unique (user_name),
    constraint fk_users_role foreign key (id_role)
        references Role(id_role) on delete restrict on update cascade
);

create table Properties(
    id_property int not null,
    internal_code varchar(20) not null,
    address varchar(35) not null,
    area_m2 float not null check (area_m2 > 0), 
    price decimal(10,2) not null check (price > 0), 
    active boolean not null default true,
    date_register datetime default current_timestamp not null,
    update_date datetime not null default current_timestamp on update current_timestamp,
    cover_url varchar(255) not null,
    id_state int not null,
    id_user varchar(36) not null,
    id_property_type int not null,
    constraint pk_properties primary key (id_property),
    constraint uk_properties_internal_code unique (internal_code),
    constraint fk_properties_state foreign key (id_state)
        references State(id_state) on delete restrict on update cascade,
    constraint fk_properties_user foreign key (id_user)
        references Users(id_user) on delete restrict on update cascade,
    constraint fk_properties_type foreign key (id_property_type)
        references PropertyType(id_property_type) on delete restrict on update cascade
);

create table PropertyImages(
    id_image int not null auto_increment,
    id_property int not null,
    images_url varchar(255) not null,
    constraint pk_property_images primary key (id_image),
    constraint fk_property_images_property foreign key (id_property)
        references Properties(id_property) on delete cascade on update cascade
);

----------------------------------------------------------------------------------
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

# READ USERS
delimiter $$
create procedure sp_read_users()
begin
    select id_user, name, last_name, email, user_name, active, id_role
    from Users
    where active = true;
end $$
delimiter ;

# READ USER BY ID
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

----------------------------------------------------------------------------------
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
                                    in cover_url_p varchar(255),
                                    in id_state_p int,
                                    in id_user_p varchar(36),
                                    in id_property_type_p int)
begin
    insert into Properties (
        id_property, internal_code, address, area_m2, price, active,
        date_register, update_date, cover_url, id_state, id_user, id_property_type)
    values (
        id_property_p, internal_code_p, address_p, area_m2_p, price_p, active_p,
        date_register_p, update_date_p, cover_url_p, id_state_p, id_user_p, id_property_type_p);
end $$
delimiter ;

# READ PROPERTIES
delimiter $$
create procedure sp_read_properties()
begin
    select * from Properties where active = true;
end $$
delimiter ;

# READ PROPERTY BY ID
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
                                    in cover_url_p varchar(255),
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
        cover_url = cover_url_p,
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

# SEARCH PROPERTY
delimiter $$
create procedure sp_search_properties(in search_text varchar(50),
										in min_price decimal(10,2),
										in max_price decimal(10,2),
										in min_area float,
										in max_area float,
										in p_id_state int,
										in p_id_property_type int
)
begin
    select id_property, internal_code, address, area_m2, price, active, 
           date_register, update_date, cover_url, id_state, id_user, id_property_type 
    from Properties
    where active = true
      and (search_text is null or address like concat('%', search_text, '%') or internal_code like concat('%', search_text, '%'))
      and (min_price is null or price >= min_price)
      and (max_price is null or price <= max_price)
      and (min_area is null or area_m2 >= min_area)
      and (max_area is null or area_m2 <= max_area)
      and (p_id_state is null or id_state = p_id_state)
      and (p_id_property_type is null or id_property_type = p_id_property_type);
end $$
delimiter ;
----------------------------------------------------------------------------------
# CREATE ROLE
delimiter $$
create procedure sp_create_role(in id_role_p int, in name_role_p varchar(40))
begin
    insert into Role (id_role, name_role) values (id_role_p, name_role_p);
end $$
delimiter ;

# READ ROLES
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

----------------------------------------------------------------------------------
# CREATE PROPERTY TYPE
delimiter $$
create procedure sp_create_propertytype(in id_property_type_p int, in type_name_p varchar(20))
begin
    insert into PropertyType (id_property_type, type_name) values (id_property_type_p, type_name_p);
end $$
delimiter ;

# READ PROPERTY TYPES
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

----------------------------------------------------------------------------------
# CREATE STATE
delimiter $$
create procedure sp_create_state(in id_state_p int, in state_name_p varchar(20))
begin
    insert into State (id_state, state_name) values (id_state_p, state_name_p);
end $$
delimiter ;

# READ STATES
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

----------------------------------------------------------------------------------
# CREATE PROPERTY IMAGE
delimiter $$
create procedure sp_create_property_image(in id_property_p int,
                                          in images_url_p varchar(255))
begin
    insert into PropertyImages (id_property, images_url)
    values (id_property_p, images_url_p);
end $$
delimiter ;

# READ PROPERTY IMAGES
delimiter $$
create procedure sp_read_property_images(in id_property_p int)
begin
    select id_image, id_property, images_url
    from PropertyImages
    where id_property = id_property_p;
end $$
delimiter ;

# READ PROPERTY IMAGE BY ID
delimiter $$
create procedure sp_read_property_image_by_id(in id_image_p int)
begin
    select id_image, id_property, images_url
    from PropertyImages
    where id_image = id_image_p;
end $$
delimiter ;

# UPDATE PROPERTY IMAGE
delimiter $$
create procedure sp_update_property_image(in id_image_p int,
                                          in id_property_p int,
                                          in images_url_p varchar(255))
begin
    update PropertyImages
    set
        id_property = id_property_p,
        images_url = images_url_p
    where id_image = id_image_p;
end $$
delimiter ;

# DELETE PROPERTY IMAGE 
delimiter $$
create procedure sp_delete_property_image(in id_image_p int)
begin
    delete from PropertyImages where id_image = id_image_p;
end $$
delimiter ;