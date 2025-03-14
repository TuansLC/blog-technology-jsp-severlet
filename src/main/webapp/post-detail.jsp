<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết bài viết - Blog Công nghệ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body>
<%@ include file="/menu/header.jsp" %>

<div class="container mt-4">
    <div class="row">
        <div class="col-lg-8">
            <article>
                <h1 class="mb-4">Tiêu đề bài viết</h1>
                <p class="text-muted">Đăng ngày 15 tháng 4, 2023 bởi Nguyễn Văn A</p>
                <img src="/placeholder.svg?height=400&width=800" class="img-fluid rounded mb-4" alt="Ảnh bài viết">
                <div class="mb-4">
                    <h5>Đánh giá:</h5>
                    <div id="rating" class="mb-2">
                        <i class="bi bi-star" data-rating="1"></i>
                        <i class="bi bi-star" data-rating="2"></i>
                        <i class="bi bi-star" data-rating="3"></i>
                        <i class="bi bi-star" data-rating="4"></i>
                        <i class="bi bi-star" data-rating="5"></i>
                    </div>
                    <p>Đánh giá trung bình: <span id="averageRating">0</span> (<span id="totalRatings">0</span> đánh giá)</p>
                </div>
                <div class="blog-content">
                    <p>Đây là nội dung đầy đủ của bài viết. Bạn có thể viết nhiều đoạn văn, thêm hình ảnh và định dạng văn bản theo nhu cầu.</p>
                    <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>
                    <h2>Tiêu đề phụ</h2>
                    <p>Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
                </div>
            </article>

            <section class="mt-5">
                <h3>Bình luận</h3>
                <div id="comments">
                    <!-- Các bình luận hiện có sẽ được tải ở đây -->
                </div>
                <form id="commentForm" class="mt-4">
                    <div class="mb-3">
                        <label for="commentName" class="form-label">Tên</label>
                        <input type="text" class="form-control" id="commentName" required>
                    </div>
                    <div class="mb-3">
                        <label for="commentEmail" class="form-label">Email</label>
                        <input type="email" class="form-control" id="commentEmail" required>
                    </div>
                    <div class="mb-3">
                        <label for="commentContent" class="form-label">Bình luận</label>
                        <textarea class="form-control" id="commentContent" rows="3" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">Gửi bình luận</button>
                </form>
            </section>
        </div>
        <div class="col-lg-4">
            <div class="card mb-4">
                <div class="card-body">
                    <h5 class="card-title">Về tác giả</h5>
                    <p class="card-text">Nguyễn Văn A là một nhà văn đam mê chia sẻ suy nghĩ của mình về nhiều chủ đề khác nhau. Anh ấy đã viết blog hơn 5 năm.</p>
                </div>
            </div>
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Bài viết liên quan</h5>
                    <ul class="list-unstyled">
                        <li><a href="#">Một bài viết thú vị khác</a></li>
                        <li><a href="#">Bạn có thể thích cái này</a></li>
                        <li><a href="#">Hãy xem bài viết này</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/menu/footer.jsp" %>

<script src="${pageContext.request.contextPath}/js/post-detail.js"></script>
</body>
</html>