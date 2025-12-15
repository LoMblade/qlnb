-- Script để kiểm tra và tạo lại password cho admin
-- Chạy script này trong MySQL để kiểm tra password hiện tại

USE htnb;

-- Kiểm tra user admin
SELECT id, username, email, active, 
       LEFT(password, 20) as password_preview,
       role_id, department_id
FROM users 
WHERE username = 'admin';

-- Nếu password không đúng, bạn có thể tạo lại password hash bằng BCrypt
-- Password: admin123
-- Hash mới: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- Cập nhật password cho admin (nếu cần)
-- UPDATE users 
-- SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
-- WHERE username = 'admin';

