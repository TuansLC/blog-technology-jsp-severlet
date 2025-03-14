<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liên hệ - Blog Công nghệ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body>
<%@ include file="/menu/header.jsp" %>

<div class="container mt-4">
    <h1>Liên hệ</h1>
    <div class="row">
        <div class="col-md-6">
            <form>
                <div class="mb-3">
                    <label for="name" class="form-label">Họ tên</label>
                    <input type="text" class="form-control" id="name" required>
                </div>
                <div class="mb-3">
                    <label for="email" class="form-label">Địa chỉ email</label>
                    <input type="email" class="form-control" id="email" required>
                </div>
                <div class="mb-3">
                    <label for="message" class="form-label">Tin nhắn</label>
                    <textarea class="form-control" id="message" rows="5" required></textarea>
                </div>
                <button type="submit" class="btn btn-primary">Gửi tin nhắn</button>
            </form>
        </div>
        <div class="col-md-6">
            <h3>Liên hệ với tôi</h3>
            <p>Nếu bạn có bất kỳ câu hỏi hoặc ý kiến nào, hãy liên hệ với tôi qua biểu mẫu hoặc thông qua các phương thức sau:</p>
            <ul class="list-unstyled">
                <li>Email: your.email@example.com</li>
                <li>Twitter: @yourtwitterhandle</li>
                <li>LinkedIn: Your Name</li>
            </ul>
        </div>
    </div>
</div>

<%@ include file="/menu/footer.jsp" %>
</body>
</html>