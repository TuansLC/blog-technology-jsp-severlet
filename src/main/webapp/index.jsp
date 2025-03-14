<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Blog Công nghệ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/menu/header.jsp" />

<div class="container mt-4">
    <h1>Chào mừng đến với Blog Công nghệ</h1>

    <!-- Featured Posts Slider -->
    <div id="featuredPosts" class="carousel slide mb-4" data-bs-ride="carousel">
        <div class="carousel-inner">
            <div class="carousel-item active">
                <img src="/placeholder.svg?height=400&width=800" class="d-block w-100" alt="Featured Post 1">
                <div class="carousel-caption d-none d-md-block">
                    <h5>Bài viết nổi bật 1</h5>
                    <p>Mô tả bài viết nổi bật 1</p>
                </div>
            </div>
            <div class="carousel-item">
                <img src="/placeholder.svg?height=400&width=800" class="d-block w-100" alt="Featured Post 2">
                <div class="carousel-caption d-none d-md-block">
                    <h5>Bài viết nổi bật 2</h5>
                    <p>Mô tả bài viết nổi bật 2</p>
                </div>
            </div>
        </div>
        <button class="carousel-control-prev" type="button" data-bs-target="#featuredPosts" data-bs-slide="prev">
            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Previous</span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#featuredPosts" data-bs-slide="next">
            <span class="carousel-control-next-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Next</span>
        </button>
    </div>

    <div class="row">
        <div class="col-md-8">
            <article class="blog-post">
                <div class="row">
                    <div class="col-md-4">
                        <img src="/placeholder.svg?height=200&width=300" class="img-fluid rounded" alt="First Blog Post Image">
                    </div>
                    <div class="col-md-8">
                        <h2>Bài viết đầu tiên</h2>
                        <p class="text-muted">Đăng ngày 1 tháng 4, 2023</p>
                        <p>Đây là nội dung của bài viết đầu tiên. Bạn có thể viết về bất cứ điều gì ở đây.</p>
                        <a href="${pageContext.request.contextPath}/post/1" class="btn btn-primary">Đọc thêm</a>
                    </div>
                </div>
            </article>
            <article class="blog-post mt-4">
                <div class="row">
                    <div class="col-md-4">
                        <img src="/placeholder.svg?height=200&width=300" class="img-fluid rounded" alt="Second Blog Post Image">
                    </div>
                    <div class="col-md-8">
                        <h2>Bài viết thứ hai</h2>
                        <p class="text-muted">Đăng ngày 5 tháng 4, 2023</p>
                        <p>Đây là nội dung của bài viết thứ hai. Bạn có thể viết về bất cứ điều gì ở đây.</p>
                        <a href="${pageContext.request.contextPath}/post/2" class="btn btn-primary">Đọc thêm</a>
                    </div>
                </div>
            </article>
            <!-- Thêm phân trang -->
            <nav aria-label="Điều hướng trang" class="mt-4">
                <ul class="pagination justify-content-center">
                    <li class="page-item disabled">
                        <a class="page-link" href="#" tabindex="-1" aria-disabled="true">Trước</a>
                    </li>
                    <li class="page-item active" aria-current="page">
                        <a class="page-link" href="#">1</a>
                    </li>
                    <li class="page-item">
                        <a class="page-link" href="#">2</a>
                    </li>
                    <li class="page-item">
                        <a class="page-link" href="#">3</a>
                    </li>
                    <li class="page-item">
                        <a class="page-link" href="#">Tiếp</a>
                    </li>
                </ul>
            </nav>
        </div>
        <div class="col-md-4">
            <div class="card mb-4">
                <div class="card-body">
                    <h5 class="card-title">Về tôi</h5>
                    <p class="card-text">Một mô tả ngắn về bản thân hoặc blog của bạn.</p>
                    <a href="${pageContext.request.contextPath}/about" class="btn btn-secondary">Tìm hiểu thêm</a>
                </div>
            </div>
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Đăng ký nhận tin</h5>
                    <p class="card-text">Cập nhật với các bài viết mới nhất của chúng tôi!</p>
                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#newsletterModal">Đăng ký</button>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/menu/footer.jsp" />

<!-- Newsletter Modal -->
<div class="modal fade" id="newsletterModal" tabindex="-1" aria-labelledby="newsletterModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="newsletterModalLabel">Đăng ký nhận bản tin</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="newsletterForm">
                    <div class="mb-3">
                        <label for="newsletterEmail" class="form-label">Địa chỉ email</label>
                        <input type="email" class="form-control" id="newsletterEmail" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Đăng ký</button>
                </form>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/script.js.js"></script>
</body>
</html>