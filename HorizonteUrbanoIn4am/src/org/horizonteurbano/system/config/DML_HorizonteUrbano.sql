use HorizonteUrbano_in4am_root;

-----------------------------------------------------------------
#  ROLES 

# CREATE ROLE
call sp_create_role(1, '');
call sp_create_role(2, '');
call sp_create_role(3, '');

# READ ROLES
call sp_read_roles();

# UPDATE ROLE
call sp_update_role(1, '');

# DELETE ROLE
call sp_delete_role(3);

-----------------------------------------------------------------
# STATES 

# CREATE STATE
call sp_create_state(1, '');
call sp_create_state(2, '');
call sp_create_state(3, '');
call sp_create_state(4, '');

# READ STATES
call sp_read_states();

# UPDATE STATE
call sp_update_state(1, '');

# DELETE STATE
call sp_delete_state(4);

-----------------------------------------------------------------
# PROPERTY TYPES

# CREATE PROPERTY TYPE
call sp_create_propertytype(1, '');
call sp_create_propertytype(2, '');
call sp_create_propertytype(3, '');
call sp_create_propertytype(4, '');

# READ PROPERTY TYPES
call sp_read_propertytypes();

# UPDATE PROPERTY TYPE
call sp_update_propertytype(1, '');

# DELETE PROPERTY TYPE
call sp_delete_propertytype(4);

-----------------------------------------------------------------
# USERS

# CREATE USER
call sp_create_user('', '', '', '', '', true, 1);
call sp_create_user('', '', '', '', '', true, 2);

# READ USERS
call sp_read_users();

# READ USER BY ID
set @user1 = (select id_user from Users where id_role = 1 limit 1);
call sp_read_userid(@user1);

# UPDATE USER
call sp_update_user(@user1, '', '', '', '', '', true, 1);

# LOGIN USER
call sp_login_user('', '');

# DELETE USER
call sp_delete_user(@user1);

-----------------------------------------------------------------
# PROPERTIES

# Obtener un usuario activo para las propiedades
set @user2 = (select id_user from Users limit 1);

# CREATE PROPERTY
call sp_create_property(1, '', '', 1.00, 1.00, true, now(), now(), '', 1, @user2, 1);
call sp_create_property(2, '', '', 1.00, 1.00, true, now(), now(), '', 1, @user2, 2);

# READ PROPERTIES
call sp_read_properties();

# READ PROPERTY BY ID
call sp_read_propertyid(1);

# UPDATE PROPERTY
call sp_update_property(1, '', '', 1.00, 1.00, true, now(), now(), '', 1, @user2, 1);

# DELETE PROPERTY 
call sp_delete_property(2);