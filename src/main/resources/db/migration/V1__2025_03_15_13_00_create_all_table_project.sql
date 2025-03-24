-- Tạo bảng Users
CREATE TABLE users
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100),
    avatar_url    VARCHAR(255),
    bio           TEXT,
    role          ENUM('ADMIN', 'AUTHOR', 'USER') DEFAULT 'USER',
    last_login    TIMESTAMP,
    is_active     BOOLEAN   DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tạo bảng Categories
CREATE TABLE categories
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    slug        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tạo bảng Posts
CREATE TABLE posts
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    slug           VARCHAR(255) NOT NULL UNIQUE,
    content        LONGTEXT     NOT NULL,
    summary        TEXT,
    featured_image VARCHAR(255),
    author_id      INT          NOT NULL,
    status         ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED') DEFAULT 'DRAFT',
    is_featured    BOOLEAN   DEFAULT FALSE,
    view_count     INT       DEFAULT 0,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    published_at   TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Tạo bảng Post_Categories
CREATE TABLE post_categories
(
    post_id     INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (post_id, category_id),
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
);

-- Tạo bảng Tags
CREATE TABLE tags
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50) NOT NULL UNIQUE,
    slug       VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng Post_Tags
CREATE TABLE post_tags
(
    post_id INT NOT NULL,
    tag_id  INT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
);

-- Tạo bảng Comments
CREATE TABLE comments
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    post_id      INT  NOT NULL,
    user_id      INT,
    parent_id    INT,
    content      TEXT NOT NULL,
    author_name  VARCHAR(100),
    author_email VARCHAR(100),
    status       ENUM('PENDING', 'APPROVED', 'SPAM') DEFAULT 'PENDING',
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL,
    FOREIGN KEY (parent_id) REFERENCES comments (id) ON DELETE CASCADE
);

-- Tạo bảng Ratings
CREATE TABLE ratings
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    post_id      INT NOT NULL,
    user_id      INT,
    rating_value INT NOT NULL CHECK (rating_value BETWEEN 1 AND 5),
    ip_address   VARCHAR(45),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL,
    UNIQUE KEY unique_user_post_rating (user_id, post_id)
);

-- Tạo bảng Subscribers
CREATE TABLE subscribers
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(100) NOT NULL UNIQUE,
    status     ENUM('ACTIVE', 'UNSUBSCRIBED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tạo bảng Contact_Messages
CREATE TABLE contact_messages
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL,
    message    TEXT         NOT NULL,
    status     ENUM('NEW', 'READ', 'REPLIED') DEFAULT 'NEW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tạo bảng Related_Posts
CREATE TABLE related_posts
(
    post_id         INT NOT NULL,
    related_post_id INT NOT NULL,
    PRIMARY KEY (post_id, related_post_id),
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    FOREIGN KEY (related_post_id) REFERENCES posts (id) ON DELETE CASCADE
);

-- Tạo bảng Settings
CREATE TABLE settings
(
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    setting_key         VARCHAR(50) NOT NULL UNIQUE,
    setting_value       TEXT,
    setting_description TEXT,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Thêm dữ liệu mẫu cho Categories
INSERT INTO categories (name, slug, description)
VALUES ('Công nghệ', 'cong-nghe', 'Tin tức về công nghệ mới nhất'),
       ('Phần mềm', 'phan-mem', 'Thông tin về phần mềm và ứng dụng'),
       ('AI', 'ai', 'Trí tuệ nhân tạo và ứng dụng'),
       ('Web', 'web', 'Phát triển web và xu hướng mới'),
       ('Mobile', 'mobile', 'Phát triển ứng dụng di động');

-- Thêm dữ liệu mẫu cho Settings
INSERT INTO settings (setting_key, setting_value, setting_description)
VALUES ('site_title', 'Blog Công nghệ', 'Tiêu đề trang web'),
       ('site_description', 'Blog chia sẻ kiến thức về công nghệ', 'Mô tả trang web'),
       ('posts_per_page', '10', 'Số bài viết hiển thị trên mỗi trang'),
       ('enable_comments', 'true', 'Cho phép bình luận'),
       ('enable_ratings', 'true', 'Cho phép đánh giá');

-- Thêm dữ liệu mẫu cho Users
INSERT INTO users (username, email, password_hash, full_name, role)
VALUES ('admin', 'admin@example.com',
        '$2a$10$xJQx.XVpYX8wgQx9yvzO8.K1QlKdE1.KVs2/ZYwW4y5eJdOUvYUQe', 'Admin User', 'ADMIN'),
       ('author', 'author@example.com',
        '$2a$10$xJQx.XVpYX8wgQx9yvzO8.K1QlKdE1.KVs2/ZYwW4y5eJdOUvYUQe', 'Author User', 'AUTHOR'),
       ('user', 'user@example.com', '$2a$10$xJQx.XVpYX8wgQx9yvzO8.K1QlKdE1.KVs2/ZYwW4y5eJdOUvYUQe',
        'Normal User', 'USER');