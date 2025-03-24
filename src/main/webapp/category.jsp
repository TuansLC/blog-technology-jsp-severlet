<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${category.name} - Blog Công nghệ</title>
    <meta name="description" content="Danh sách bài viết trong danh mục ${category.name}">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/menu/header.jsp" />

<div class="container mt-4">
    <div class="row">
        <div class="col-lg-8">
            <h1 class="mb-4">Danh mục: ${category.name}</h1>

            <c:if test="${not empty category.description}">
                <div class="category-description mb-4">
                    <p>${category.description}</p>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty posts}">
                    <div class="alert alert-info">
                        Không có bài viết nào trong danh mục này.
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Danh sách bài viết -->
                    <div class="row">
                        <c:forEach var="post" items="${posts}">
                            <div class="col-md-12 mb-4">
                                <div class="card">
                                    <div class="row g-0">
                                        <c:if test="${not empty post.featuredImage}">
                                            <div class="col-md-4">
                                                <img src="${post.featuredImage}" class="img-fluid rounded-start h-100 object-fit-cover" alt="${post.title}">
                                            </div>
                                        </c:if>
                                        <div class="${not empty post.featuredImage ? 'col-md-8' : 'col-md-12'}">
                                            <div class="card-body">
                                                <h5 class="card-title">
                                                    <a href="${pageContext.request.contextPath}/post/${post.slug}" class="text-decoration-none">${post.title}</a>
                                                </h5>
                                                <p class="card-text">${post.summary}</p>
                                                <p class="card-text">
                                                    <small class="text-muted">
                                                        <i class="bi bi-calendar"></i>
                                                        <fmt:parseDate value="${post.publishedAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                                        <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy" />
                                                        <span class="ms-3"><i class="bi bi-person"></i> ${post.author.fullName}</span>
                                                        <span class="ms-3"><i class="bi bi-eye"></i> ${post.viewCount} lượt xem</span>
                                                    </small>
                                                </p>
                                                <a href="${pageContext.request.contextPath}/post/${post.slug}" class="btn btn-primary btn-sm">Đọc tiếp</a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Phân trang -->
                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Page navigation">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/category/${category.slug}?page=${currentPage - 1}" aria-label="Previous">
                                        <span aria-hidden="true">&laquo;</span>
                                    </a>
                                </li>

                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/category/${category.slug}?page=${i}">${i}</a>
                                    </li>
                                </c:forEach>

                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/category/${category.slug}?page=${currentPage + 1}" aria-label="Next">
                                        <span aria-hidden="true">&raquo;</span>
                                    </a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Sidebar -->
        <div class="col-lg-4">
            <div class="position-sticky" style="top: 2rem;">
                <!-- Danh mục -->
                <div class="card mb-4">
                    <div class="card-header">Danh mục</div>
                    <div class="card-body">
                        <ul class="list-unstyled mb-0">
                            <c:forEach var="cat" items="${categories}">
                                <li class="mb-2 ${cat.id == category.id ? 'fw-bold' : ''}">
                                    <a href="${pageContext.request.contextPath}/category/${cat.slug}" class="text-decoration-none">
                                            ${cat.name}
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>

                <!-- Bài viết mới nhất -->
                <div class="card mb-4">
                    <div class="card-header">Bài viết mới nhất</div>
                    <div class="card-body">
                        <c:forEach var="recentPost" items="${recentPosts}">
                            <div class="mb-3">
                                <h6 class="mb-1">
                                    <a href="${pageContext.request.contextPath}/post/${recentPost.slug}" class="text-decoration-none">
                                            ${recentPost.title}
                                    </a>
                                </h6>
                                <small class="text-muted">
                                    <fmt:parseDate value="${recentPost.publishedAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                    <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy" />
                                </small>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <!-- Đăng ký nhận tin -->
                <div class="card">
                    <div class="card-header">Đăng ký nhận tin</div>
                    <div class="card-body">
                        <p>Nhận thông báo khi có bài viết mới</p>
                        <form action="${pageContext.request.contextPath}/subscribe" method="post">
                            <div class="input-group">
                                <input type="email" class="form-control" placeholder="Email của bạn" name="email" required>
                                <button class="btn btn-primary" type="submit">Đăng ký</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/menu/footer.jsp" />

<!-- JavaScript -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>