drop database if exists HorizonteUrbano_in4am_root;
create database HorizonteUrbano_in4am_root;
use HorizonteUrbano_in4am_root;

create table Role(
    id_role INT NOT NULL,
    name_role VARCHAR(40) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_role PRIMARY KEY (id_role)
);

create table PropertyType(
    id_property_type INT NOT NULL,
    type_name VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_property_type PRIMARY KEY (id_property_type)
);

create table State(
    id_state INT NOT NULL,
    state_name VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_state PRIMARY KEY (id_state)
);

create table Users(
    id_user VARCHAR(36) NOT NULL,
    name VARCHAR(40) NOT NULL,
    last_name VARCHAR(40) NOT NULL,
    password VARCHAR(80) NOT NULL,
    email VARCHAR(40) NOT NULL,
    user_name VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    id_role INT NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id_user),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_user_name UNIQUE (user_name),
    CONSTRAINT fk_users_role FOREIGN KEY (id_role)
        REFERENCES Role(id_role) ON DELETE RESTRICT ON UPDATE CASCADE
);

create table Properties(
    id_property INT NOT NULL,
    internal_code VARCHAR(20) NOT NULL,
    address VARCHAR(35) NOT NULL,
    area_m2 FLOAT NOT NULL CHECK (area_m2 > 0), 
    price DECIMAL(10,2) NOT NULL CHECK (price > 0), 
    active BOOLEAN NOT NULL DEFAULT TRUE,
    date_register DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    cover_url VARCHAR(255) NOT NULL,
    id_state INT NOT NULL,
    id_user VARCHAR(36) NOT NULL,
    id_property_type INT NOT NULL,
    CONSTRAINT pk_properties PRIMARY KEY (id_property),
    CONSTRAINT uk_properties_internal_code UNIQUE (internal_code),
    CONSTRAINT fk_properties_state FOREIGN KEY (id_state)
        REFERENCES State(id_state) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_properties_user FOREIGN KEY (id_user)
        REFERENCES Users(id_user) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_properties_type FOREIGN KEY (id_property_type)
        REFERENCES PropertyType(id_property_type) ON DELETE RESTRICT ON UPDATE CASCADE
);

create table PropertyImages(
    id_image INT NOT NULL AUTO_INCREMENT,
    id_property INT NOT NULL,
    images_url VARCHAR(255) NOT NULL,
    CONSTRAINT pk_property_images PRIMARY KEY (id_image),
    CONSTRAINT fk_property_images_property FOREIGN KEY (id_property)
        REFERENCES Properties(id_property) ON DELETE RESTRICT ON UPDATE CASCADE
);

#----------------------------------------------------------------------------------
# CREATE USER
DELIMITER $$
	create procedure sp_create_user(IN name_p VARCHAR(40),
									IN last_name_p VARCHAR(40),
									IN password_p VARCHAR(80),
									IN email_p VARCHAR(40),
									IN user_name_p VARCHAR(30),
									IN active_p BOOLEAN,
									IN id_role_p INT)
		begin
			insert into Users (id_user, name, last_name, password, email, user_name, active, id_role)
			values (uuid(), name_p, last_name_p, password_p, email_p, user_name_p, active_p, id_role_p);
		end $$
DELIMITER ;

# READ USERS
DELIMITER $$
	create procedure sp_read_users()
	begin
		select id_user, name, last_name, email, user_name, active, id_role
		from Users
		where active = true;
	end $$
DELIMITER ;

# READ USER BY ID
DELIMITER $$
	create procedure sp_read_userid(IN id_user_p VARCHAR(36))
	begin
		select id_user, name, last_name, email, user_name, active, id_role
		from Users
		where id_user = id_user_p;
	end $$
DELIMITER ;

# UPDATE USER
DELIMITER $$
	create procedure sp_update_user(IN id_user_p VARCHAR(36),
									IN name_p VARCHAR(40),
									IN last_name_p VARCHAR(40),
									IN password_p VARCHAR(80),
									IN email_p VARCHAR(40),
									IN user_name_p VARCHAR(30),
									IN active_p BOOLEAN,
									IN id_role_p INT)
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
DELIMITER ;

# DELETE USER
DELIMITER $$
	create procedure sp_delete_user(IN id_user_p VARCHAR(36))
	begin
		update Users set active = false where id_user = id_user_p;
	end $$
DELIMITER ;

# LOGIN USER
DELIMITER $$
	create procedure sp_login_user(	IN identifier_p VARCHAR(40),
									IN password_p VARCHAR(80))
	begin
		select id_user, name, last_name, email, user_name, active, id_role
		from Users
		where (email = identifier_p or user_name = identifier_p)
		and password = password_p
		and active = true;
	end $$
DELIMITER ;

#----------------------------------------------------------------------------------
# CREATE PROPERTY
DELIMITER $$
	create procedure sp_create_property(IN id_property_p INT,
										IN internal_code_p VARCHAR(20),
										IN address_p VARCHAR(35),
										IN area_m2_p FLOAT,
										IN price_p DECIMAL(10,2),
										IN active_p BOOLEAN,
										IN date_register_p DATETIME,
										IN update_date_p DATETIME,
										IN cover_url_p VARCHAR(255),
										IN id_state_p INT,
										IN id_user_p VARCHAR(36),
											IN id_property_type_p INT)
	begin
		insert into Properties (
			id_property, internal_code, address, area_m2, price, active,
			date_register, update_date, cover_url, id_state, id_user, id_property_type)
		values (
			id_property_p, internal_code_p, address_p, area_m2_p, price_p, active_p,
			date_register_p, update_date_p, cover_url_p, id_state_p, id_user_p, id_property_type_p);
	end $$
DELIMITER ;

# READ PROPERTIES
DELIMITER $$
	create procedure sp_read_properties()
	begin
		select * from Properties where active = true;
	end $$
DELIMITER ;

# READ PROPERTY BY ID
DELIMITER $$
	create procedure sp_read_propertyid(IN id_property_p INT)
	begin
		select * from Properties where id_property = id_property_p;
	end $$
DELIMITER ;

# UPDATE PROPERTY
DELIMITER $$
	create procedure sp_update_property(IN id_property_p INT,
										IN internal_code_p VARCHAR(20),
										IN address_p VARCHAR(35),
										IN area_m2_p FLOAT,
										IN price_p DECIMAL(10,2),
										IN active_p BOOLEAN,
										IN cover_url_p VARCHAR(255),
										IN id_state_p INT,
										IN id_user_p VARCHAR(36),
										IN id_property_type_p INT)
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
DELIMITER ;

# DELETE PROPERTY 
DELIMITER $$
	create procedure sp_delete_property(IN id_property_p INT)
	begin
		update Properties set active = false where id_property = id_property_p;
	end $$
DELIMITER ;

# SEARCH PROPERTY
DELIMITER $$
	create procedure sp_search_properties(IN search_text VARCHAR(50),
											IN min_price DECIMAL(10,2),
											IN max_price DECIMAL(10,2),
											IN min_area FLOAT,
											IN max_area FLOAT,
											IN p_id_state INT,
											IN p_id_property_type INT
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
DELIMITER ;
#----------------------------------------------------------------------------------
# CREATE ROLE
DELIMITER $$
	create procedure sp_create_role(IN id_role_p INT, IN name_role_p VARCHAR(40))
	begin
		insert into Role (id_role, name_role) values (id_role_p, name_role_p);
	end $$
DELIMITER ;

# READ ROLES
DELIMITER $$
	create procedure sp_read_roles()
	begin
		select * from Role where active = true;
	end $$
DELIMITER ;

# UPDATE ROLE
DELIMITER $$
	create procedure sp_update_role(IN id_role_p INT, IN name_role_p VARCHAR(40))
	begin
		update Role set name_role = name_role_p where id_role = id_role_p;
	end $$
DELIMITER ;

# DELETE ROLE
DELIMITER $$
	create procedure sp_delete_role(IN id_role_p INT)
	begin
		update Role set active = false where id_role = id_role_p;
	end $$
DELIMITER ;

#----------------------------------------------------------------------------------
# CREATE PROPERTY TYPE
DELIMITER $$
	create procedure sp_create_propertytype(IN id_property_type_p INT, IN type_name_p VARCHAR(20))
	begin
		insert into PropertyType (id_property_type, type_name) values (id_property_type_p, type_name_p);
	end $$
DELIMITER ;

# READ PROPERTY TYPES
DELIMITER $$
	create procedure sp_read_propertytypes()
	begin
		select * from PropertyType where active = true;
	end $$
DELIMITER ;

# UPDATE PROPERTY TYPE
DELIMITER $$
	create procedure sp_update_propertytype(IN id_property_type_p INT, IN type_name_p VARCHAR(20))
	begin
		update PropertyType set type_name = type_name_p where id_property_type = id_property_type_p;
	end $$
DELIMITER ;

# DELETE PROPERTY TYPE
DELIMITER $$
	create procedure sp_delete_propertytype(IN id_property_type_p INT)
	begin
		update PropertyType set active = false where id_property_type = id_property_type_p;
	end $$
DELIMITER ;

#----------------------------------------------------------------------------------
# CREATE STATE
DELIMITER $$
	create procedure sp_create_state(IN id_state_p INT, IN state_name_p VARCHAR(20))
	begin
		insert into State (id_state, state_name) values (id_state_p, state_name_p);
	end $$
DELIMITER ;

# READ STATES
DELIMITER $$
	create procedure sp_read_states()
	begin
		select * from State where active = true;
	end $$
DELIMITER ;

# UPDATE STATE
DELIMITER $$
	create procedure sp_update_state(IN id_state_p INT, IN state_name_p VARCHAR(20))
	begin
		update State set state_name = state_name_p where id_state = id_state_p;
	end $$
DELIMITER ;

# DELETE STATE 
DELIMITER $$
	create procedure sp_delete_state(IN id_state_p INT)
	begin
		update State set active = false where id_state = id_state_p;
	end $$
DELIMITER ;

#----------------------------------------------------------------------------------
# CREATE PROPERTY IMAGE
DELIMITER $$
	create procedure sp_create_property_image(IN id_property_p INT,
											  IN images_url_p VARCHAR(255))
	begin
		insert into PropertyImages (id_property, images_url)
		values (id_property_p, images_url_p);
	end $$
DELIMITER ;

# READ PROPERTY IMAGES
DELIMITER $$
	create procedure sp_read_property_images(IN id_property_p INT)
	begin
		select id_image, id_property, images_url
		from PropertyImages
		where id_property = id_property_p;
	end $$
DELIMITER ;

# READ PROPERTY IMAGE BY ID
DELIMITER $$
	create procedure sp_read_property_image_by_id(IN id_image_p INT)
	begin
		select id_image, id_property, images_url
		from PropertyImages
		where id_image = id_image_p;
	end $$
DELIMITER ;

# UPDATE PROPERTY IMAGE
DELIMITER $$
	create procedure sp_update_property_image(IN id_image_p INT,
											  IN id_property_p INT,
											  IN images_url_p VARCHAR(255))
	begin
		update PropertyImages
		set
			id_property = id_property_p,
			images_url = images_url_p
		where id_image = id_image_p;
	end $$
DELIMITER ;

# DELETE PROPERTY IMAGE 
DELIMITER $$
	create procedure sp_delete_property_image(IN id_image_p INT)
	begin
		delete from PropertyImages where id_image = id_image_p;
	end $$
DELIMITER ;