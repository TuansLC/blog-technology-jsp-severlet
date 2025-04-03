<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản lý liên hệ - Blog Công nghệ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/menu/header.jsp"/>

<div class="container-fluid">
    <div class="row">
        
        <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
            <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h1 class="h2">Quản lý liên hệ</h1>
                <div class="btn-toolbar mb-2 mb-md-0">
                    <div class="btn-group me-2">
                        <span class="badge bg-primary">Tổng số: ${totalContacts}</span>
                    </div>
                    <div class="dropdown">
                        <button class="btn btn-sm btn-outline-secondary dropdown-toggle" type="button" id="filterDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="bi bi-funnel"></i> Lọc
                        </button>
                        <ul class="dropdown-menu" aria-labelledby="filterDropdown">
                            <li><a class="dropdown-item ${status == null ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/contacts?page=1">Tất cả</a></li>
                            <li><a class="dropdown-item ${status == 'NEW' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/contacts?status=NEW&page=1">Mới</a></li>
                            <li><a class="dropdown-item ${status == 'READ' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/contacts?status=READ&page=1">Đã đọc</a></li>
                            <li><a class="dropdown-item ${status == 'REPLIED' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/contacts?status=REPLIED&page=1">Đã trả lời</a></li>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Họ tên</th>
                            <th>Email</th>
                            <th>Tin nhắn</th>
                            <th>Trạng thái</th>
                            <th>Thời gian</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${contacts}" var="contact">
                            <tr>
                                <td>${contact.id}</td>
                                <td>${contact.name}</td>
                                <td>${contact.email}</td>
                                <td>${fn:substring(contact.message, 0, 50)}${fn:length(contact.message) > 50 ? '...' : ''}</td>
                                <td>
                                    <span class="badge bg-${contact.status == 'NEW' ? 'primary' : 
                                                         contact.status == 'READ' ? 'info' : 'success'}">
                                        ${contact.status}
                                    </span>
                                </td>
                                <td>
                                    <fmt:parseDate value="${contact.createdAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                    <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm" />
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-secondary" 
                                                data-bs-toggle="modal" data-bs-target="#contactDetailModal"
                                                data-id="${contact.id}"
                                                data-name="${contact.name}"
                                                data-email="${contact.email}"
                                                data-message="${contact.message}"
                                                data-status="${contact.status}"
                                                data-time="<fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm" />">
                                            <i class="bi bi-eye"></i> Chi tiết
                                        </button>
                                        
                                        <c:if test="${contact.status == 'NEW'}">
                                            <form action="${pageContext.request.contextPath}/admin/contacts" method="post" style="display: inline;">
                                                <input type="hidden" name="action" value="markAsRead">
                                                <input type="hidden" name="contactId" value="${contact.id}">
                                                <input type="hidden" name="page" value="${currentPage}">
                                                <input type="hidden" name="status" value="${status}">
                                                <button type="submit" class="btn btn-sm btn-info ms-1">
                                                    <i class="bi bi-check"></i> Đánh dấu đã đọc
                                                </button>
                                            </form>
                                        </c:if>
                                        <c:if test="${contact.status != 'REPLIED'}">
                                            <form action="${pageContext.request.contextPath}/admin/contacts" method="post" style="display: inline;">
                                                <input type="hidden" name="action" value="markAsReplied">
                                                <input type="hidden" name="contactId" value="${contact.id}">
                                                <input type="hidden" name="page" value="${currentPage}">
                                                <input type="hidden" name="status" value="${status}">
                                                <button type="submit" class="btn btn-sm btn-success ms-1">
                                                    <i class="bi bi-reply"></i> Đánh dấu đã trả lời
                                                </button>
                                            </form>
                                        </c:if>
                                        <button type="button" class="btn btn-sm btn-primary ms-1" 
                                                onclick="window.location.href='mailto:${contact.email}'">
                                            <i class="bi bi-envelope"></i> Gửi email
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            
            <!-- Phân trang -->
            <c:if test="${totalPages > 1}">
                <nav aria-label="Page navigation">
                    <ul class="pagination justify-content-center">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/admin/contacts?page=${currentPage - 1}${status != null ? '&status='.concat(status) : ''}" tabindex="-1">Trước</a>
                        </li>
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/contacts?page=${i}${status != null ? '&status='.concat(status) : ''}">${i}</a>
                            </li>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/admin/contacts?page=${currentPage + 1}${status != null ? '&status='.concat(status) : ''}">Sau</a>
                        </li>
                    </ul>
                </nav>
            </c:if>
        </main>
    </div>
</div>

<!-- Thêm modal xem chi tiết -->
<div class="modal fade" id="contactDetailModal" tabindex="-1" aria-labelledby="contactDetailModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="contactDetailModalLabel">Chi tiết liên hệ</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="row mb-3">
                    <div class="col-md-6">
                        <p><strong>Họ tên:</strong> <span id="detailName"></span></p>
                        <p><strong>Email:</strong> <span id="detailEmail"></span></p>
                    </div>
                    <div class="col-md-6">
                        <p><strong>Trạng thái:</strong> <span id="detailStatus"></span></p>
                        <p><strong>Thời gian:</strong> <span id="detailTime"></span></p>
                    </div>
                </div>
                <div class="row">
                    <div class="col-12">
                        <p><strong>Tin nhắn:</strong></p>
                        <div class="p-3 bg-light rounded" id="detailMessage"></div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                <button type="button" class="btn btn-primary" id="detailEmailBtn">
                    <i class="bi bi-envelope"></i> Gửi email
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Thêm script xử lý modal -->
<script>
document.addEventListener('DOMContentLoaded', function() {
    // Xử lý hiển thị modal chi tiết
    const contactDetailModal = document.getElementById('contactDetailModal');
    if (contactDetailModal) {
        contactDetailModal.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-id');
            const name = button.getAttribute('data-name');
            const email = button.getAttribute('data-email');
            const message = button.getAttribute('data-message');
            const status = button.getAttribute('data-status');
            const time = button.getAttribute('data-time');
            
            document.getElementById('detailName').textContent = name;
            document.getElementById('detailEmail').textContent = email;
            document.getElementById('detailMessage').textContent = message;
            document.getElementById('detailStatus').textContent = status;
            document.getElementById('detailTime').textContent = time;
            
            document.getElementById('detailEmailBtn').onclick = function() {
                window.location.href = 'mailto:' + email;
            };
        });
    }
});
</script>

<%@ include file="/menu/footer.jsp" %>
</body>
</html> 