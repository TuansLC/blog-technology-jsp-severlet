-- Thêm tags mẫu
INSERT INTO tags (name, slug) VALUES
('JavaScript', 'javascript'),
('Java', 'java'),
('Python', 'python'),
('React', 'react'),
('Spring Boot', 'spring-boot'),
('Machine Learning', 'machine-learning'),
('Docker', 'docker'),
('Kubernetes', 'kubernetes'),
('DevOps', 'devops'),
('Cloud Computing', 'cloud-computing');

-- Thêm bài viết mẫu
INSERT INTO posts (title, slug, content, summary, featured_image, author_id, status, is_featured, view_count, published_at) VALUES
(
    'Giới thiệu về Spring Boot 3.0',
    'gioi-thieu-spring-boot-3',
    'Spring Boot 3.0 là một bước tiến quan trọng trong việc phát triển ứng dụng Java. Phiên bản này mang đến nhiều tính năng mới và cải tiến đáng kể...\n\n
    ## Tính năng mới\n
    - Hỗ trợ Java 17\n
    - Native image support\n
    - Observability\n
    - Project CRaC support\n\n
    ## Cải tiến hiệu suất\n
    Spring Boot 3.0 mang lại hiệu suất tốt hơn thông qua...',
    'Khám phá những tính năng mới và cải tiến trong Spring Boot 3.0, framework phát triển ứng dụng Java phổ biến nhất hiện nay.',
    '/images/posts/spring-boot-3.jpg',
    1,
    'PUBLISHED',
    true,
    1250,
    CURRENT_TIMESTAMP - INTERVAL 5 DAY
),
(
    'Tìm hiểu về Trí tuệ nhân tạo và Machine Learning',
    'tim-hieu-ve-ai-va-machine-learning',
    'Trí tuệ nhân tạo (AI) và Machine Learning đang thay đổi cách chúng ta tương tác với công nghệ...\n\n
    ## AI là gì?\n
    Trí tuệ nhân tạo là khả năng của máy tính để mô phỏng trí thông minh của con người...\n\n
    ## Machine Learning\n
    Machine Learning là một phần của AI, tập trung vào việc phát triển các thuật toán cho phép máy tính học từ dữ liệu...',
    'Giới thiệu tổng quan về AI và Machine Learning, các ứng dụng và tương lai của công nghệ này.',
    '/images/posts/ai-ml.jpg',
    2,
    'PUBLISHED',
    true,
    980,
    CURRENT_TIMESTAMP - INTERVAL 3 DAY
),
(
    'Docker và Kubernetes trong phát triển hiện đại',
    'docker-kubernetes-trong-phat-trien-hien-dai',
    'Container hóa đã trở thành một phần không thể thiếu trong phát triển phần mềm hiện đại...\n\n
    ## Docker là gì?\n
    Docker là nền tảng phần mềm cho phép bạn đóng gói và chạy ứng dụng trong các container...\n\n
    ## Kubernetes\n
    Kubernetes là hệ thống điều phối container mã nguồn mở được phát triển bởi Google...',
    'Tìm hiểu về Docker và Kubernetes, hai công nghệ container hóa đang thay đổi cách chúng ta phát triển và triển khai ứng dụng.',
    '/images/posts/docker-kubernetes.jpg',
    1,
    'PUBLISHED',
    false,
    756,
    CURRENT_TIMESTAMP - INTERVAL 2 DAY
),
(
    'React 18 và những tính năng mới',
    'react-18-va-nhung-tinh-nang-moi',
    'React 18 giới thiệu nhiều tính năng mới thú vị cho các nhà phát triển frontend...\n\n
    ## Concurrent Mode\n
    Concurrent Mode là một trong những tính năng quan trọng nhất trong React 18...\n\n
    ## Automatic Batching\n
    React 18 mở rộng batching tự động cho tất cả các updates...',
    'Khám phá những tính năng mới trong React 18 và cách chúng cải thiện hiệu suất ứng dụng của bạn.',
    '/images/posts/react-18.jpg',
    2,
    'PUBLISHED',
    true,
    890,
    CURRENT_TIMESTAMP - INTERVAL 1 DAY
),
(
    'Cloud Computing và tương lai của công nghệ',
    'cloud-computing-va-tuong-lai-cong-nghe',
    'Cloud Computing đang định hình lại cách các doanh nghiệp vận hành và phát triển...\n\n
    ## Các mô hình Cloud\n
    - Infrastructure as a Service (IaaS)\n
    - Platform as a Service (PaaS)\n
    - Software as a Service (SaaS)\n\n
    ## Lợi ích của Cloud Computing\n
    Cloud Computing mang lại nhiều lợi ích như...',
    'Tìm hiểu về Cloud Computing, các mô hình triển khai và tác động của nó đến tương lai công nghệ.',
    '/images/posts/cloud-computing.jpg',
    1,
    'PUBLISHED',
    false,
    645,
    CURRENT_TIMESTAMP
);

-- Thêm dữ liệu cho bảng post_categories
INSERT INTO post_categories (post_id, category_id) VALUES
(1, 2), -- Spring Boot -> Phần mềm
(1, 4), -- Spring Boot -> Web
(2, 3), -- AI/ML -> AI
(3, 1), -- Docker/Kubernetes -> Công nghệ
(4, 4), -- React -> Web
(5, 1); -- Cloud Computing -> Công nghệ

-- Thêm dữ liệu cho bảng post_tags
INSERT INTO post_tags (post_id, tag_id) VALUES
(1, 2), -- Spring Boot -> Java
(1, 5), -- Spring Boot -> Spring Boot
(2, 6), -- AI/ML -> Machine Learning
(3, 7), -- Docker/Kubernetes -> Docker
(3, 8), -- Docker/Kubernetes -> Kubernetes
(3, 9), -- Docker/Kubernetes -> DevOps
(4, 1), -- React -> JavaScript
(4, 4), -- React -> React
(5, 10); -- Cloud Computing -> Cloud Computing

-- Thêm một số comments mẫu
INSERT INTO comments (post_id, user_id, content, status) VALUES
(1, 3, 'Bài viết rất hữu ích! Tôi đang tìm hiểu về Spring Boot.', 'APPROVED'),
(1, 2, 'Cảm ơn tác giả đã chia sẻ kiến thức.', 'APPROVED'),
(2, 3, 'AI thực sự là tương lai của công nghệ!', 'APPROVED'),
(3, 2, 'Docker đã thay đổi cách tôi phát triển phần mềm.', 'APPROVED'),
(4, 3, 'React 18 có nhiều cải tiến thú vị.', 'APPROVED');

-- Thêm một số đánh giá mẫu
INSERT INTO ratings (post_id, user_id, rating_value) VALUES
(1, 2, 5),
(1, 3, 4),
(2, 2, 5),
(3, 3, 4),
(4, 2, 5);

-- Thêm subscribers mẫu
INSERT INTO subscribers (email, status) VALUES
('subscriber1@example.com', 'ACTIVE'),
('subscriber2@example.com', 'ACTIVE'),
('subscriber3@example.com', 'ACTIVE'); 