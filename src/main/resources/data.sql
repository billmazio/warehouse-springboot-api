-- Insert sizes
INSERT IGNORE INTO sizes (id, name) VALUES (1, 'EXTRA SMALL');
INSERT IGNORE INTO sizes (id, name) VALUES (2, 'SMALL');
INSERT IGNORE INTO sizes (id, name) VALUES (3, 'MEDIUM');
INSERT IGNORE INTO sizes (id, name) VALUES (4, 'LARGE');
INSERT IGNORE INTO sizes (id, name) VALUES (5, 'EXTRA LARGE');

-- Insert default store
INSERT IGNORE INTO stores (id, title, address, is_system_entity, status)
VALUES (1, 'ΚΕΝΤΡΙΚΑ', 'Αθήνα', 0, 'ACTIVE');

-- Insert role
INSERT IGNORE INTO clothes_manager.roles (id, name, tag, created_at, updated_at)
VALUES (1, 'SUPER_ADMIN', 'Super Admin', NOW(), NOW());

-- Insert admin user (password: Admin!1234)
INSERT IGNORE INTO users (id, username, password, store_id, is_system_entity, status, created_at, updated_at)
VALUES (1, 'admin', '$2a$10$Pi/jMKcF8P4IhsAWPXFkBefqG2QuLtgaH5wqRrjVJt.xQr9Innui2', 1, 0, 'ACTIVE', NOW(), NOW());

-- Assign SUPER_ADMIN role to admin user
INSERT IGNORE INTO clothes_manager.user_roles (user_id, role_id)
VALUES (1, 1);

-- Insert material
INSERT IGNORE INTO materials (id, quantity, text, size_id, store_id)
VALUES (1, 1, 'Μπλούζα', 3, 1);