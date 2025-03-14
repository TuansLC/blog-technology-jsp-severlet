<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">Blog Công nghệ</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/">Trang chủ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/about">Giới thiệu</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/contact">Liên hệ</a>
                </li>
            </ul>
            <form class="d-flex me-2">
                <input class="form-control me-2" type="search" placeholder="Tìm kiếm" aria-label="Search">
                <button class="btn btn-outline-success" type="submit">Tìm</button>
            </form>
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/sign-in">Đăng nhập</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/sign-up">Đăng ký</a>
                </li>
                <li class="nav-item">
                    <button class="btn btn-link nav-link" id="darkModeToggle">
                        <i class="bi bi-moon"></i>
                    </button>
                </li>
            </ul>
        </div>
    </div>
</nav>