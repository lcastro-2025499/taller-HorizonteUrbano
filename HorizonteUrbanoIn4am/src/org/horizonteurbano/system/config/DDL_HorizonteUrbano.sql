drop database if exists HorizonteUrbano_in4am;
create database HorizonteUrbano_in4am;
use HorizonteUrbano_in4am;

-- ---------------------------------------------------------------------
-- TABLAS
-- ---------------------------------------------------------------------

create table Role(
    id_role INT NOT NULL AUTO_INCREMENT,
    name_role VARCHAR(40) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_role PRIMARY KEY (id_role)
);

create table PropertyType(
    id_type INT NOT NULL AUTO_INCREMENT,
    name_type VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_property_type PRIMARY KEY (id_type)
);

create table State(
    id_state INT NOT NULL AUTO_INCREMENT,
    name_state VARCHAR(20) NOT NULL,
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
    id_property INT NOT NULL AUTO_INCREMENT,
    internal_code VARCHAR(20) NOT NULL,
    address VARCHAR(35) NOT NULL,
    area_m2 FLOAT NOT NULL CHECK (area_m2 > 0),
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    date_register DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    cover_url VARCHAR(255) NULL,
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
        REFERENCES PropertyType(id_type) ON DELETE RESTRICT ON UPDATE CASCADE
);

create table PropertyImages(
    id_image INT NOT NULL AUTO_INCREMENT,
    id_property INT NOT NULL,
    images_url VARCHAR(255) NOT NULL,
    CONSTRAINT pk_property_images PRIMARY KEY (id_image),
    CONSTRAINT fk_property_images_property FOREIGN KEY (id_property)
        REFERENCES Properties(id_property) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- ---------------------------------------------------------------------
-- DATOS SEMILLA (necesarios para que Users/Properties puedan crearse,
-- ya que dependen de estas tablas por FK). Ajusta los nombres si el
-- equipo definio otros en el DPP.
-- ---------------------------------------------------------------------

INSERT INTO Role (name_role) VALUES
    ('Administrador'),  -- id_role = 1
    ('Asesor'),          -- id_role = 2
    ('Gerente');         -- id_role = 3

INSERT INTO PropertyType (name_type) VALUES
    ('Casa'),
    ('Apartamento'),
    ('Terreno'),
    ('Local Comercial'),
    ('Bodega');

INSERT INTO State (name_state) VALUES
    ('Disponible'),
    ('Reservado'),
    ('Vendido'),
    ('Rentado');

-- =====================================================================
-- STORED PROCEDURES
-- =====================================================================

-- ---------------------------------------------------------------------
-- USERS
-- ---------------------------------------------------------------------
DELIMITER $$
CREATE PROCEDURE sp_create_user(IN name_p VARCHAR(40),
                                 IN last_name_p VARCHAR(40),
                                 IN password_p VARCHAR(80),
                                 IN email_p VARCHAR(40),
                                 IN user_name_p VARCHAR(30),
                                 IN active_p BOOLEAN,
                                 IN id_role_p INT)
BEGIN
    INSERT INTO Users (id_user, name, last_name, password, email, user_name, active, id_role)
    VALUES (uuid(), name_p, last_name_p, password_p, email_p, user_name_p, active_p, id_role_p);
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_read_users()
BEGIN
    SELECT id_user, name, last_name, email, user_name, active, id_role
    FROM Users
    WHERE active = true;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_read_userid(IN id_user_p VARCHAR(36))
BEGIN
    SELECT id_user, name, last_name, email, user_name, active, id_role
    FROM Users
    WHERE id_user = id_user_p;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_update_user(IN id_user_p VARCHAR(36),
                                 IN name_p VARCHAR(40),
                                 IN last_name_p VARCHAR(40),
                                 IN email_p VARCHAR(40),
                                 IN user_name_p VARCHAR(30),
                                 IN active_p BOOLEAN,
                                 IN id_role_p INT)
BEGIN
    UPDATE Users
    SET
        name = name_p,
        last_name = last_name_p,
        email = email_p,
        user_name = user_name_p,
        active = active_p,
        id_role = id_role_p
    WHERE id_user = id_user_p;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_update_user_password(IN id_user_p VARCHAR(36),
                                          IN password_p VARCHAR(80))
BEGIN
    UPDATE Users SET password = password_p WHERE id_user = id_user_p;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_delete_user(IN id_user_p VARCHAR(36))
BEGIN
    UPDATE Users SET active = false WHERE id_user = id_user_p;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_login_user(IN identifier_p VARCHAR(40),
                                IN password_p VARCHAR(80))
BEGIN
    SELECT id_user, name, last_name, email, user_name, password, active, id_role
    FROM Users
    WHERE (email = identifier_p OR user_name = identifier_p)
      AND active = true;
END $$
DELIMITER ;

-- ---------------------------------------------------------------------
-- PROPERTIES
-- ---------------------------------------------------------------------
DELIMITER $$
CREATE PROCEDURE sp_create_property(IN internal_code_p VARCHAR(20),
                                     IN address_p VARCHAR(35),
                                     IN area_m2_p FLOAT,
                                     IN price_p DECIMAL(10,2),
                                     IN active_p BOOLEAN,
                                     IN cover_url_p VARCHAR(255),
                                     IN id_state_p INT,
                                     IN id_user_p VARCHAR(36),
                                     IN id_property_type_p INT)
BEGIN
    INSERT INTO Properties (
        internal_code, address, area_m2, price, active,
        cover_url, id_state, id_user, id_property_type)
    VALUES (
        internal_code_p, address_p, area_m2_p, price_p, active_p,
        cover_url_p, id_state_p, id_user_p, id_property_type_p);
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_read_properties()
BEGIN
    SELECT * FROM Properties WHERE active = true;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_read_propertyid(IN id_property_p INT)
BEGIN
    SELECT * FROM Properties WHERE id_property = id_property_p;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_update_property(IN id_property_p INT,
                                     IN internal_code_p VARCHAR(20),
                                     IN address_p VARCHAR(35),
                                     IN area_m2_p FLOAT,
                                     IN price_p DECIMAL(10,2),
                                     IN active_p BOOLEAN,
                                     IN cover_url_p VARCHAR(255),
                                     IN id_state_p INT,
                                     IN id_user_p VARCHAR(36),
                                     IN id_property_type_p INT)
BEGIN
    UPDATE Properties
    SET
        internal_code = internal_code_p,
        address = address_p,
        area_m2 = area_m2_p,
        price = price_p,
        active = active_p,
        cover_url = cover_url_p,
        id_state = id_state_p,
        id_user = id_user_p,
        id_property_type = id_property_type_p
    WHERE id_property = id_property_p;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_change_property_state(IN id_property_p INT, IN id_state_p INT)
BEGIN
    UPDATE Properties SET id_state = id_state_p WHERE id_property = id_property_p;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_delete_property(IN id_property_p INT)
BEGIN
    UPDATE Properties SET active = false WHERE id_property = id_property_p;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_search_properties(IN search_text VARCHAR(50),
                                       IN min_price DECIMAL(10,2),
                                       IN max_price DECIMAL(10,2),
                                       IN min_area FLOAT,
                                       IN max_area FLOAT,
                                       IN p_id_state INT,
                                       IN p_id_property_type INT)
BEGIN
    SELECT id_property, internal_code, address, area_m2, price, active,
           date_register, update_date, cover_url, id_state, id_user, id_property_type
    FROM Properties
    WHERE active = true
      AND (search_text IS NULL OR address LIKE CONCAT('%', search_text, '%') OR internal_code LIKE CONCAT('%', search_text, '%'))
      AND (min_price IS NULL OR price >= min_price)
      AND (max_price IS NULL OR price <= max_price)
      AND (min_area IS NULL OR area_m2 >= min_area)
      AND (max_area IS NULL OR area_m2 <= max_area)
      AND (p_id_state IS NULL OR id_state = p_id_state)
      AND (p_id_property_type IS NULL OR id_property_type = p_id_property_type);
END $$
DELIMITER ;

-- ---------------------------------------------------------------------
-- ROLE
-- ---------------------------------------------------------------------
DELIMITER $$
CREATE PROCEDURE sp_create_role(IN name_role_p VARCHAR(40))
BEGIN
    INSERT INTO Role (name_role) VALUES (name_role_p);
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_read_roles()
BEGIN
    SELECT * FROM Role WHERE active = true;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_update_role(IN id_role_p INT, IN name_role_p VARCHAR(40))
BEGIN
    UPDATE Role SET name_role = name_role_p WHERE id_role = id_role_p;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_delete_role(IN id_role_p INT)
BEGIN
    UPDATE Role SET active = false WHERE id_role = id_role_p;
END $$
DELIMITER ;

-- ---------------------------------------------------------------------
-- PROPERTY TYPE
-- ---------------------------------------------------------------------
DELIMITER $$
CREATE PROCEDURE sp_create_propertytype(IN name_type_p VARCHAR(20))
BEGIN
    INSERT INTO PropertyType (name_type) VALUES (name_type_p);
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_read_propertytypes()
BEGIN
    SELECT * FROM PropertyType WHERE active = true;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_update_propertytype(IN id_type_p INT, IN name_type_p VARCHAR(20))
BEGIN
    UPDATE PropertyType SET name_type = name_type_p WHERE id_type = id_type_p;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_delete_propertytype(IN id_type_p INT)
BEGIN
    UPDATE PropertyType SET active = false WHERE id_type = id_type_p;
END $$
DELIMITER ;

-- ---------------------------------------------------------------------
-- STATE
-- ---------------------------------------------------------------------
DELIMITER $$
CREATE PROCEDURE sp_create_state(IN name_state_p VARCHAR(20))
BEGIN
    INSERT INTO State (name_state) VALUES (name_state_p);
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_read_states()
BEGIN
    SELECT * FROM State WHERE active = true;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_update_state(IN id_state_p INT, IN name_state_p VARCHAR(20))
BEGIN
    UPDATE State SET name_state = name_state_p WHERE id_state = id_state_p;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_delete_state(IN id_state_p INT)
BEGIN
    UPDATE State SET active = false WHERE id_state = id_state_p;
END $$
DELIMITER ;

-- ---------------------------------------------------------------------
-- PROPERTY IMAGES
-- ---------------------------------------------------------------------
DELIMITER $$
CREATE PROCEDURE sp_create_property_image(IN id_property_p INT,
                                           IN images_url_p VARCHAR(255))
BEGIN
    INSERT INTO PropertyImages (id_property, images_url)
    VALUES (id_property_p, images_url_p);
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_read_property_images(IN id_property_p INT)
BEGIN
    SELECT id_image, id_property, images_url
    FROM PropertyImages
    WHERE id_property = id_property_p;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_read_property_image_by_id(IN id_image_p INT)
BEGIN
    SELECT id_image, id_property, images_url
    FROM PropertyImages
    WHERE id_image = id_image_p;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_update_property_image(IN id_image_p INT,
                                           IN id_property_p INT,
                                           IN images_url_p VARCHAR(255))
BEGIN
    UPDATE PropertyImages
    SET
        id_property = id_property_p,
        images_url = images_url_p
    WHERE id_image = id_image_p;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_delete_property_image(IN id_image_p INT)
BEGIN
    DELETE FROM PropertyImages WHERE id_image = id_image_p;
END $$
DELIMITER ;

select * from Users;