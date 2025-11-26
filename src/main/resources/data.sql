-- Insert default store
INSERT INTO stores (id, title, address, is_system_entity, status)
VALUES (1, 'Default Store', 'Default Address', 0, 'ACTIVE');

-- Insert admin user (password: Admin!1234)
INSERT INTO users (id, username, password, store_id, is_system_entity, status)
VALUES (1, 'admin', '$2a$10$pOz8haNENrVZcZYcEwN14.QMBqhtM4j/XS1Pp1pv41IgJDnP.Qqle', 1, 0, 'ACTIVE');