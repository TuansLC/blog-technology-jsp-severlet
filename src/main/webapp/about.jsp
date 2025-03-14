<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Giới thiệu - Blog Công nghệ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body>
<%@ include file="/menu/header.jsp" %>

<div class="container mt-4">
    <h1>Giới thiệu</h1>
    <div class="row">
        <div class="col-md-8">
            <p>Xin chào! Tôi là tác giả của blog này. Ở đây bạn có thể viết mô tả chi tiết về bản thân, sở thích và mục đích của blog.</p>
            <p>Hãy chia sẻ về nền tảng, chuyên môn và những gì độc giả có thể mong đợi từ các bài đăng trên blog của bạn.</p>
        </div>
        <div class="col-md-4">
            <img src="/placeholder.svg?height=300&width=300" alt="Ảnh tác giả" class="img-fluid rounded">
        </div>
    </div>
</div>

<%@ include file="/menu/footer.jsp" %>
</body>
</html>