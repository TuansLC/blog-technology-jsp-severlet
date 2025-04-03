<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Blog Công nghệ</title>
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/menu/header.jsp"/>

<!-- Main Content -->
<div class="container mt-4">
    <!-- Featured Posts Carousel -->
    <div id="featuredCarousel" class="carousel slide mb-4" data-bs-ride="carousel">
        <div class="carousel-inner">
            <c:forEach items="${featuredPosts}" var="post" varStatus="status">
                <div class="carousel-item ${status.first ? 'active' : ''}">
                    <img src="${post.featuredImage}" class="d-block w-100" alt="${post.title}">
                    <div class="carousel-caption d-none d-md-block">
                        <h2>${post.title}</h2>
                        <p class="text-muted">
                            <i class="bi bi-person"></i> ${post.author.fullName} |
                            <i class="bi bi-calendar"></i> ${post.publishedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} |
                            <i class="bi bi-eye"></i> ${post.viewCount} lượt xem
                        </p>
                        <p>${post.summary}</p>
                        <a href="${pageContext.request.contextPath}/post/${post.slug}" class="btn btn-primary">Đọc thêm</a>
                    </div>
                </div>
            </c:forEach>
        </div>
        <!-- Carousel controls -->
        <button class="carousel-control-prev" type="button" data-bs-target="#featuredCarousel" data-bs-slide="prev">
            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Previous</span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#featuredCarousel" data-bs-slide="next">
            <span class="carousel-control-next-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Next</span>
        </button>
    </div>

    <!-- Latest Posts -->
    <div class="row">
        <c:forEach items="${posts}" var="post">
            <div class="col-md-6 col-lg-4 mb-4">
                <div class="card h-100">
                    <img src="${post.featuredImage}" class="card-img-top" alt="${post.title}">
                    <div class="card-body">
                        <h5 class="card-title">${post.title}</h5>
                        <p class="card-text text-muted">
                            <i class="bi bi-person"></i> ${post.author.fullName} |
                            <i class="bi bi-calendar"></i> ${post.publishedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} |
                            <i class="bi bi-eye"></i> ${post.viewCount} lượt xem
                        </p>
                        <p class="card-text">${post.summary}</p>
                        <a href="${pageContext.request.contextPath}/post/${post.slug}" class="btn btn-primary">Đọc thêm</a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>

    <!-- Pagination -->
    <c:if test="${totalPages > 1}">
        <nav aria-label="Page navigation">
            <ul class="pagination justify-content-center">
                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                    <a class="page-link" href="?page=${currentPage - 1}" tabindex="-1">Trước</a>
                </li>
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                        <a class="page-link" href="?page=${i}">${i}</a>
                    </li>
                </c:forEach>
                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="?page=${currentPage + 1}">Sau</a>
                </li>
            </ul>
        </nav>
    </c:if>
</div>

<jsp:include page="/menu/footer.jsp"/>
</body>
</html>