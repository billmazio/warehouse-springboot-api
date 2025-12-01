-- Insert sizes
INSERT IGNORE INTO sizes (id, name) VALUES (1, 'EXTRA SMALL');
INSERT IGNORE INTO sizes (id, name) VALUES (2, 'SMALL');
INSERT IGNORE INTO sizes (id, name) VALUES (3, 'MEDIUM');
INSERT IGNORE INTO sizes (id, name) VALUES (4, 'LARGE');
INSERT IGNORE INTO sizes (id, name) VALUES (5, 'EXTRA LARGE');

-- Insert stores
INSERT IGNORE INTO stores (id, title, address, is_system_entity, status)
VALUES (1, 'ΚΕΝΤΡΙΚΑ', 'Αθήνα', 1, 'ACTIVE');

-- Insert roles
INSERT IGNORE INTO warehouse_db.roles (id, created_at, updated_at, name, tag)
VALUES (1, NOW(), NOW(), 'SUPER_ADMIN', 'Super Admin');

INSERT IGNORE INTO warehouse_db.roles (id, created_at, updated_at, name, tag)
VALUES (2, NOW(), NOW(), 'LOCAL_ADMIN', 'Local Admin');

INSERT IGNORE INTO users (id, username, password, store_id, is_system_entity, status)
VALUES
(1, 'admin', '$2a$10$Pi/jMKcF8P4IhsAWPXFkBefqG2QuLtgaH5wqRrjVJt.xQr9Innui2', 1, 0, 'ACTIVE'),
(2, 'testuser', '$2a$10$Pi/jMKcF8P4IhsAWPXFkBefqG2QuLtgaH5wqRrjVJt.xQr9Innui2', 2, 0, 'INACTIVE');

INSERT IGNORE INTO warehouse_db.user_roles (user_id, role_id)
VALUES (1, 1), (2, 2);

-- Insert material
INSERT IGNORE INTO materials (id, quantity, text, size_id, store_id)
VALUES (1, 1, 'Μπλούζα', 3, 1);

