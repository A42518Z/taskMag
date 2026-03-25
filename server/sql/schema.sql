CREATE DATABASE IF NOT EXISTS taskmag DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE taskmag;

DROP TABLE IF EXISTS base_user_info;
CREATE TABLE base_user_info (
    id VARCHAR(32) NOT NULL PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL,
    login_name VARCHAR(100) NOT NULL,
    state VARCHAR(20) DEFAULT 'ENABLED',
    phone VARCHAR(30) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO base_user_info (id, user_name, login_name, state, phone) VALUES
('u001', '张三', 'zhangsan', 'ENABLED', '13800000001'),
('u002', '李四', 'lisi', 'ENABLED', '13800000002'),
('u003', '王五', 'wangwu', 'DISABLED', '13800000003');

DROP TABLE IF EXISTS trade_order;
CREATE TABLE trade_order (
    id VARCHAR(32) NOT NULL PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    price DECIMAL(12,2) NOT NULL DEFAULT 0,
    remark VARCHAR(255) DEFAULT NULL,
    address VARCHAR(255) DEFAULT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'CREATED',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO trade_order (id, order_no, customer_name, price, remark, address, status) VALUES
('o001', 'TD2026001', '张三', 99.90, '首单', '上海市浦东新区', 'CREATED'),
('o002', 'TD2026002', '李四', 168.00, '加急', '北京市朝阳区', 'PAID');
