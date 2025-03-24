<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
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

    <!-- Thông báo đăng ký nhận tin -->
    <c:if test="${not empty subscribeMessage}">
        <div class="alert alert-${subscribeMessageType} alert-dismissible fade show" role="alert">
                ${subscribeMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- Featured Posts Slider -->
    <div id="featuredPosts" class="carousel slide mb-4" data-bs-ride="carousel">
        <div class="carousel-inner">
            <c:forEach items="${featuredPosts}" var="post" varStatus="status">
                <div class="carousel-item ${status.index == 0 ? 'active' : ''}">
                    <c:choose>
                        <c:when test="${not empty post.featuredImage}">
                            <img src="${post.featuredImage}" class="d-block w-100" alt="${post.title}">
                        </c:when>
                        <c:otherwise>
                            <img src="${pageContext.request.contextPath}/images/placeholder.jpg" class="d-block w-100" alt="${post.title}">
                        </c:otherwise>
                    </c:choose>
                    <div class="carousel-caption d-none d-md-block">
                        <h5>${post.title}</h5>
                        <p>${post.summary}</p>
                        <a href="${pageContext.request.contextPath}/post/${post.slug}" class="btn btn-primary">Đọc thêm</a>
                    </div>
                </div>
            </c:forEach>

            <!-- Nếu không có bài viết nổi bật, hiển thị mặc định -->
            <c:if test="${empty featuredPosts}">
                <div class="carousel-item active">
                    <img src="${pageContext.request.contextPath}/images/placeholder.jpg" class="d-block w-100" alt="Featured Post">
                    <div class="carousel-caption d-none d-md-block">
                        <h5>Chưa có bài viết nổi bật</h5>
                        <p>Hãy quay lại sau để xem các bài viết nổi bật của chúng tôi.</p>
                    </div>
                </div>
            </c:if>
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
            <!-- Danh sách bài viết -->
            <c:forEach items="${posts}" var="post">
                <article class="blog-post mb-4">
                    <div class="row">
                        <div class="col-md-4">
                            <c:choose>
                                <c:when test="${not empty post.featuredImage}">
                                    <img src="${post.featuredImage}" class="img-fluid rounded" alt="${post.title}">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/images/placeholder.jpg" class="img-fluid rounded" alt="${post.title}">
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="col-md-8">
                            <h2>${post.title}</h2>
                            <p class="text-muted">
                                <i class="bi bi-person"></i> ${post.author.fullName} |
                                <i class="bi bi-calendar"></i> <fmt:formatDate value="${post.publishedAt}" pattern="dd/MM/yyyy" /> |
                                <i class="bi bi-eye"></i> ${post.viewCount} lượt xem
                            </p>
                            <p>${post.summary}</p>
                            <a href="${pageContext.request.contextPath}/post/${post.slug}" class="btn btn-primary">Đọc thêm</a>
                        </div>
                    </div>
                </article>
            </c:forEach>

            <!-- Nếu không có bài viết, hiển thị thông báo -->
            <c:if test="${empty posts}">
                <div class="alert alert-info">
                    Chưa có bài viết nào. Hãy quay lại sau!
                </div>
            </c:if>

            <!-- Phân trang -->
            <c:if test="${totalPages > 1}">
                <nav aria-label="Điều hướng trang" class="mt-4">
                    <ul class="pagination justify-content-center">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/?page=${currentPage - 1}" tabindex="-1" ${currentPage == 1 ? 'aria-disabled="true"' : ''}>Trước</a>
                        </li>

                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <li class="page-item ${currentPage == i ? 'active' : ''}" ${currentPage == i ? 'aria-current="page"' : ''}>
                                <a class="page-link" href="${pageContext.request.contextPath}/?page=${i}">${i}</a>
                            </li>
                        </c:forEach>

                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/?page=${currentPage + 1}" ${currentPage == totalPages ? 'aria-disabled="true"' : ''}>Tiếp</a>
                        </li>
                    </ul>
                </nav>
            </c:if>
        </div>
        <div class="col-md-4">
            <!-- Sidebar -->
            <div class="card mb-4">
                <div class="card-body">
                    <h5 class="card-title">Về tôi</h5>
                    <p class="card-text">Một mô tả ngắn về bản thân hoặc blog của bạn.</p>
                    <a href="${pageContext.request.contextPath}/about" class="btn btn-secondary">Tìm hiểu thêm</a>
                </div>
            </div>

            <!-- Danh mục -->
            <div class="card mb-4">
                <div class="card-header">
                    Danh mục
                </div>
                <ul class="list-group list-group-flush">
                    <c:forEach items="${categories}" var="category">
                        <li class="list-group-item">
                            <a href="${pageContext.request.contextPath}/category/${category.slug}" class="text-decoration-none">
                                    ${category.name}
                            </a>
                        </li>
                    </c:forEach>
                </ul>
            </div>

            <!-- Đăng ký nhận tin -->
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
                <form action="${pageContext.request.contextPath}/subscribe" method="post">
                    <input type="hidden" name="redirectUrl" value="${pageContext.request.contextPath}/">
                    <div class="mb-3">
                        <label for="email" class="form-label">Địa chỉ email</label>
                        <input type="email" class="form-control" id="email" name="email" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Đăng ký</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Hiển thị modal đăng ký nếu có lỗi -->
<c:if test="${not empty subscribeMessage && subscribeMessageType eq 'danger'}">
    <script>
      document.addEventListener('DOMContentLoaded', function() {
        var newsletterModal = new bootstrap.Modal(document.getElementById('newsletterModal'));
        newsletterModal.show();
      });
    </script>
</c:if>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>