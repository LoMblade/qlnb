-- ============================================
-- TẠO DATABASE
-- ============================================
CREATE DATABASE IF NOT EXISTS htnb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE htnb;

-- ============================================
-- TẠO CÁC BẢNG
-- ============================================

-- 1. Bảng DEPARTMENTS
CREATE TABLE departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

-- 2. Bảng ROLES
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    parent_id BIGINT NULL,
    FOREIGN KEY (parent_id) REFERENCES roles(id) ON DELETE SET NULL
);

-- 3. Bảng PERMISSIONS
CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    resource_type VARCHAR(50) NOT NULL,
    action_type VARCHAR(50) NOT NULL
);

-- 4. Bảng USERS
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    role_id BIGINT NULL,
    department_id BIGINT NULL,
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE SET NULL,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

-- 5. Bảng ROLE_PERMISSIONS
CREATE TABLE role_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    UNIQUE KEY unique_role_permission (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- 6. Bảng REFRESH_TOKENS
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL
);

-- ============================================
-- INSERT DỮ LIỆU
-- ============================================

-- 1. INSERT ROLES
INSERT INTO roles (role_code, name, description, parent_id) VALUES
('ADMIN', 'Administrator', 'Full system administrator', NULL)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description);

INSERT INTO roles (role_code, name, description, parent_id) VALUES
('TEAM_LEAD', 'Team Leader', 'Team leader', (SELECT id FROM roles WHERE role_code = 'ADMIN'))
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), parent_id=(SELECT id FROM roles WHERE role_code = 'ADMIN'));

INSERT INTO roles (role_code, name, description, parent_id) VALUES
('USER', 'User', 'Regular user', (SELECT id FROM roles WHERE role_code = 'TEAM_LEAD'))
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), parent_id=(SELECT id FROM roles WHERE role_code = 'TEAM_LEAD'));

-- 2. INSERT DEPARTMENTS
INSERT INTO departments (department_code, name, description) VALUES
('IT', 'Information Technology', 'IT Department'),
('HR', 'Human Resources', 'HR Department'),
('FINANCE', 'Finance', 'Finance Department'),
('SALES', 'Sales', 'Sales Department')
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description);

-- 3. INSERT PERMISSIONS - DEPARTMENT
INSERT INTO permissions (permission_code, name, description, resource_type, action_type) VALUES
('DEPT_CREATE', 'Create Department', 'Create department', 'DEPARTMENT', 'CREATE'),
('DEPT_READ', 'Read Department', 'View department', 'DEPARTMENT', 'READ'),
('DEPT_UPDATE', 'Update Department', 'Update department', 'DEPARTMENT', 'UPDATE'),
('DEPT_DELETE', 'Delete Department', 'Delete department', 'DEPARTMENT', 'DELETE')
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), resource_type=VALUES(resource_type), action_type=VALUES(action_type);

-- 4. INSERT PERMISSIONS - USER
INSERT INTO permissions (permission_code, name, description, resource_type, action_type) VALUES
('USER_CREATE', 'Create User', 'Create user', 'USER', 'CREATE'),
('USER_READ', 'Read User', 'View user', 'USER', 'READ'),
('USER_UPDATE', 'Update User', 'Update user', 'USER', 'UPDATE'),
('USER_DELETE', 'Delete User', 'Delete user', 'USER', 'DELETE')
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), resource_type=VALUES(resource_type), action_type=VALUES(action_type);

-- 5. INSERT PERMISSIONS - TEAM_LEAD
INSERT INTO permissions (permission_code, name, description, resource_type, action_type) VALUES
('TEAM_LEAD_CREATE', 'Create Team Lead', 'Create team lead', 'TEAM_LEAD', 'CREATE'),
('TEAM_LEAD_READ', 'Read Team Lead', 'View team lead', 'TEAM_LEAD', 'READ'),
('TEAM_LEAD_UPDATE', 'Update Team Lead', 'Update team lead', 'TEAM_LEAD', 'UPDATE'),
('TEAM_LEAD_DELETE', 'Delete Team Lead', 'Delete team lead', 'TEAM_LEAD', 'DELETE')
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), resource_type=VALUES(resource_type), action_type=VALUES(action_type);

-- 6. INSERT PERMISSIONS - ROLE
INSERT INTO permissions (permission_code, name, description, resource_type, action_type) VALUES
('ROLE_CREATE', 'Create Role', 'Create role', 'ROLE', 'CREATE'),
('ROLE_READ', 'Read Role', 'View role', 'ROLE', 'READ'),
('ROLE_UPDATE', 'Update Role', 'Update role', 'ROLE', 'UPDATE'),
('ROLE_DELETE', 'Delete Role', 'Delete role', 'ROLE', 'DELETE')
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), resource_type=VALUES(resource_type), action_type=VALUES(action_type);

-- 7. INSERT PERMISSIONS - PERMISSION
INSERT INTO permissions (permission_code, name, description, resource_type, action_type) VALUES
('PERM_CREATE', 'Create Permission', 'Create permission', 'PERMISSION', 'CREATE'),
('PERM_READ', 'Read Permission', 'View permission', 'PERMISSION', 'READ'),
('PERM_UPDATE', 'Update Permission', 'Update permission', 'PERMISSION', 'UPDATE'),
('PERM_DELETE', 'Delete Permission', 'Delete permission', 'PERMISSION', 'DELETE')
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), resource_type=VALUES(resource_type), action_type=VALUES(action_type);

-- 8. GÁN TẤT CẢ PERMISSIONS CHO ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.role_code = 'ADMIN'
ON DUPLICATE KEY UPDATE role_id=VALUES(role_id);

-- 9. INSERT USER ADMIN
-- Password: admin123
INSERT INTO users (username, password, email, role_id, department_id, active) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@example.com', 
 (SELECT id FROM roles WHERE role_code = 'ADMIN'), 
 (SELECT id FROM departments WHERE department_code = 'IT'), 
 TRUE)
ON DUPLICATE KEY UPDATE password=VALUES(password), email=VALUES(email), role_id=VALUES(role_id), department_id=VALUES(department_id);
