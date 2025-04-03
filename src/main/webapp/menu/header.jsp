<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Thêm CSS của Bootstrap và Bootstrap Icons vào phần đầu file -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">

<!-- Thêm đoạn này vào đầu file để debug -->
<div style="display: none">
    Session exists: ${not empty sessionScope.currentUser}<br>
    User role: ${sessionScope.currentUser.role}<br>
    Is admin: ${sessionScope.currentUser.role == 'ADMIN'}<br>
</div>

<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">Blog Công nghệ</a>
        
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/">Trang chủ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/category">Danh mục</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/about-us">Giới thiệu</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/contact-us">Liên hệ</a>
                </li>
                
                <!-- Menu Admin -->
                <c:if test="${not empty sessionScope.currentUser && sessionScope.currentUser.role eq 'ADMIN'}">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="bi bi-gear"></i> Quản lý
                        </a>
                        <ul class="dropdown-menu">
                            <li>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/posts">
                                    <i class="bi bi-file-text"></i> Quản lý bài viết
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/categories">
                                    <i class="bi bi-folder"></i> Quản lý danh mục
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/tags">
                                    <i class="bi bi-tags"></i> Quản lý thẻ
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/users">
                                    <i class="bi bi-people"></i> Quản lý người dùng
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/contacts">
                                    <i class="bi bi-envelope"></i> Quản lý liên hệ
                                </a>
                            </li>
                        </ul>
                    </li>
                </c:if>
            </ul>

            <!-- Search form -->
            <form class="d-flex me-3" action="${pageContext.request.contextPath}/search" method="get">
                <input class="form-control me-2" type="search" name="q" placeholder="Tìm kiếm...">
                <button class="btn btn-outline-success" type="submit">Tìm</button>
            </form>

            <!-- User menu -->
            <div class="d-flex align-items-center">
                <c:choose>
                    <c:when test="${not empty sessionScope.currentUser}">
                        <div class="nav-item dropdown">
                            <a href="#" class="nav-link dropdown-toggle" role="button" 
                               data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="bi bi-person-circle me-1"></i>
                                ${sessionScope.currentUser.username}
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/profile">
                                        <i class="bi bi-person me-2"></i>Trang cá nhân
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/my-posts">
                                        <i class="bi bi-file-text me-2"></i>Bài viết của tôi
                                    </a>
                                </li>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/sign-out">
                                        <i class="bi bi-box-arrow-right me-2"></i>Đăng xuất
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-outline-primary me-2" href="${pageContext.request.contextPath}/sign-in">
                            Đăng nhập
                        </a>
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/sign-up">
                            Đăng ký
                        </a>
                    </c:otherwise>
                </c:choose>

                <!-- Dark mode toggle -->
                <button id="darkModeToggle" class="btn btn-link nav-link ms-2">
                    <i class="bi bi-moon"></i>
                </button>
            </div>
        </div>
    </div>
</nav>

<!-- Thêm JavaScript của Bootstrap vào cuối file -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<!-- Menu Admin -->
<c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
    <!-- Thêm để debug -->
    <div style="display: none">
        User Role: ${sessionScope.currentUser.role}
        Is Admin: ${sessionScope.currentUser.role == 'ADMIN'}
    </div>
</c:if>