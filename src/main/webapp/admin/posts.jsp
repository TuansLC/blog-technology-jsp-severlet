<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản lý bài viết - Blog Công nghệ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/menu/header.jsp"/>

<div class="container-fluid">
    <div class="row">
        
        <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
            <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h1 class="h2">Quản lý bài viết</h1>
                <div class="btn-toolbar mb-2 mb-md-0">
                    <a href="${pageContext.request.contextPath}/admin/post/new" class="btn btn-sm btn-primary">
                        <i class="bi bi-plus-circle"></i> Tạo bài viết mới
                    </a>
                </div>
            </div>

            <!-- Bộ lọc -->
            <div class="row mb-3">
                <div class="col-md-12">
                    <form action="${pageContext.request.contextPath}/admin/posts" method="get" class="row g-3">
                        <div class="col-md-3">
                            <select name="status" class="form-select">
                                <option value="">-- Trạng thái --</option>
                                <option value="DRAFT" ${param.status == 'DRAFT' ? 'selected' : ''}>Bản nháp</option>
                                <option value="PUBLISHED" ${param.status == 'PUBLISHED' ? 'selected' : ''}>Đã xuất bản</option>
                                <option value="SCHEDULED" ${param.status == 'SCHEDULED' ? 'selected' : ''}>Đã lên lịch</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <select name="category" class="form-select">
                                <option value="">-- Danh mục --</option>
                                <c:forEach items="${allCategories}" var="category">
                                    <option value="${category.id}" ${param.category == category.id ? 'selected' : ''}>${category.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <input type="text" name="search" class="form-control" placeholder="Tìm kiếm..." value="${param.search}">
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary w-100">Lọc</button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Nút thêm bài viết -->
            <div class="mb-3">
                <form action="${pageContext.request.contextPath}/admin/posts" method="post">
                    <input type="hidden" name="action" value="add">
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-plus-circle"></i> Thêm bài viết
                    </button>
                </form>
            </div>

            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tiêu đề</th>
                            <th>Tác giả</th>
                            <th>Danh mục</th>
                            <th>Trạng thái</th>
                            <th>Lượt xem</th>
                            <th>Ngày tạo</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${posts}" var="post">
                            <tr>
                                <td>${post.id}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/post/${post.slug}" target="_blank" class="text-decoration-none">
                                        ${fn:substring(post.title, 0, 50)}${fn:length(post.title) > 50 ? '...' : ''}
                                    </a>
                                    <c:if test="${post.featured}">
                                        <span class="badge bg-warning text-dark ms-1">Nổi bật</span>
                                    </c:if>
                                </td>
                                <td>${post.author.fullName}</td>
                                <td>
                                    <c:forEach items="${post.categories}" var="category" varStatus="status">
                                        <span class="badge bg-secondary">${category.name}</span>
                                        ${!status.last ? ' ' : ''}
                                    </c:forEach>
                                </td>
                                <td>
                                    <span class="badge bg-${post.status == 'PUBLISHED' ? 'success' : 
                                                         post.status == 'DRAFT' ? 'secondary' : 'info'}">
                                        ${post.status}
                                    </span>
                                </td>
                                <td>${post.viewCount}</td>
                                <td>
                                    <fmt:parseDate value="${post.createdAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                    <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm" />
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <form action="${pageContext.request.contextPath}/admin/posts" method="post" class="d-inline">
                                            <input type="hidden" name="action" value="edit">
                                            <input type="hidden" name="id" value="${post.id}">
                                            <button type="submit" class="btn btn-sm btn-primary">
                                                <i class="bi bi-pencil"></i> Sửa
                                            </button>
                                        </form>
                                        <form action="${pageContext.request.contextPath}/admin/posts" method="post" class="d-inline delete-form">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="${post.id}">
                                            <button type="submit" class="btn btn-sm btn-danger">
                                                <i class="bi bi-trash"></i> Xóa
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            
            <!-- Thêm phân trang ở cuối bảng -->
            <div class="d-flex justify-content-between align-items-center mt-3">
                <div>
                    Hiển thị ${posts.size()} / ${totalPosts} bài viết
                </div>
                
                <nav aria-label="Phân trang">
                    <ul class="pagination">
                        <!-- Nút Previous -->
                        <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/admin/posts?page=${currentPage - 1}${not empty status ? '&status='.concat(status) : ''}${not empty param.category ? '&category='.concat(param.category) : ''}${not empty param.search ? '&search='.concat(param.search) : ''}" aria-label="Previous">
                                <span aria-hidden="true">&laquo;</span>
                            </a>
                        </li>
                        
                        <!-- Các trang -->
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/posts?page=${i}${not empty status ? '&status='.concat(status) : ''}${not empty param.category ? '&category='.concat(param.category) : ''}${not empty param.search ? '&search='.concat(param.search) : ''}">${i}</a>
                            </li>
                        </c:forEach>
                        
                        <!-- Nút Next -->
                        <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/admin/posts?page=${currentPage + 1}${not empty status ? '&status='.concat(status) : ''}${not empty param.category ? '&category='.concat(param.category) : ''}${not empty param.search ? '&search='.concat(param.search) : ''}" aria-label="Next">
                                <span aria-hidden="true">&raquo;</span>
                            </a>
                        </li>
                    </ul>
                </nav>
            </div>
        </main>
    </div>
</div>

<!-- Modal xóa bài viết -->
<div class="modal fade" id="deletePostModal" tabindex="-1" aria-labelledby="deletePostModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="deletePostModalLabel">Xác nhận xóa</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Bạn có chắc chắn muốn xóa bài viết <strong id="deletePostTitle"></strong>?</p>
                <p class="text-danger">Lưu ý: Hành động này không thể hoàn tác.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <form action="${pageContext.request.contextPath}/admin/post/delete" method="post">
                    <input type="hidden" id="deletePostId" name="id">
                    <button type="submit" class="btn btn-danger">Xóa</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Script xử lý modal -->
<script>
document.addEventListener('DOMContentLoaded', function() {
    // Xử lý modal xóa bài viết
    const deletePostModal = document.getElementById('deletePostModal');
    if (deletePostModal) {
        deletePostModal.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-id');
            const title = button.getAttribute('data-title');
            
            document.getElementById('deletePostId').value = id;
            document.getElementById('deletePostTitle').textContent = title;
        });
    }

    const deleteForms = document.querySelectorAll('.delete-form');
    deleteForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!confirm('Bạn có chắc chắn muốn xóa bài viết này không?')) {
                e.preventDefault();
            }
        });
    });
});
</script>

<%@ include file="/menu/footer.jsp" %>
</body>
</html> 