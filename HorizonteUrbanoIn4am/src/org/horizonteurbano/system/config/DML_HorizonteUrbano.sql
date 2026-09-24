use HorizonteUrbano_in4am;

-- =====================================================================
-- TESTEO DE DATOS
-- =====================================================================
select * from Role;
select * from State;
select * from PropertyType;
select * from Users;
select * from PropertyImages;

-- =====================================================================
-- DATOS SEMILLA
-- =====================================================================
-- ---------------------------------------------------------------------
-- PROPERTY TYPES
-- ---------------------------------------------------------------------
INSERT INTO PropertyType (name_type) VALUES
    ('Casa'),
    ('Apartamento'),
    ('Terreno'),
    ('Local Comercial'),
    ('Bodega');
    
-- ---------------------------------------------------------------------
-- ROLES
-- ---------------------------------------------------------------------
INSERT INTO Role (name_role) VALUES
    ('Administrador'),  -- id_role = 1
    ('Asesor'),         -- id_role = 2
    ('Gerente');        -- id_role = 3

-- ---------------------------------------------------------------------
-- STATES
-- ---------------------------------------------------------------------

INSERT INTO State (name_state) VALUES
    ('Disponible'),
    ('Reservado'),
    ('Vendido'),
    ('Rentado');

-- ---------------------------------------------------------------------
-- USERS (Con contraseña hasheada)
-- ---------------------------------------------------------------------
set @hashed_password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy';

call sp_create_user('admin', '123', @hashed_password, 'admin123@gmail.com', 'admin', true, 1);
call sp_create_user('asesor', '456', @hashed_password, 'asesor456@gmail.com', 'asesor', true, 2);
call sp_create_user('gerente', '789', @hashed_password, 'gerente789@gmail.com', 'gerente', true, 3);

-- ---------------------------------------------------------------------
-- PROPERTIES
-- ---------------------------------------------------------------------
set @admin = (select id_user from Users where user_name = 'admin' limit 1);

call sp_create_property('HU-001', '15 Avenida 12-34 Zona 10', 120.5, 850000.00, true, null, 1, @admin, 1);
call sp_create_property('HU-002', '5 Calle 3-45 Zona 14', 85.0, 620000.00, true, null, 1, @admin, 2);
call sp_create_property('HU-003', 'Boulevard Los Próceres Km 15', 500.0, 1500000.00, true, null, 1, @admin, 3);
call sp_create_property('HU-004', '7 Avenida 8-90 Zona 4', 200.0, 950000.00, true, null, 2, @admin, 4);

-- ---------------------------------------------------------------------
-- PROPERTY IMAGES
-- ---------------------------------------------------------------------
set @prop1 = (select id_property from Properties where internal_code = 'HU-001' limit 1);
set @prop2 = (select id_property from Properties where internal_code = 'HU-002' limit 1);

call sp_create_property_image(@prop1, 'https://example.com/hu-001-a.jpg');
call sp_create_property_image(@prop1, 'https://example.com/hu-001-b.jpg');
call sp_create_property_image(@prop2, 'https://example.com/hu-002-a.jpg');




