<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản lý thẻ - Blog Công nghệ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/menu/header.jsp"/>

<div class="container-fluid">
    <div class="row">
        
        <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
            <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h1 class="h2">Quản lý thẻ</h1>
                <div class="btn-toolbar mb-2 mb-md-0">
                    <button type="button" class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#addTagModal">
                        <i class="bi bi-plus-circle"></i> Thêm thẻ mới
                    </button>
                </div>
            </div>

            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tên thẻ</th>
                            <th>Slug</th>
                            <th>Ngày tạo</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${tags}" var="tag">
                            <tr>
                                <td>${tag.id}</td>
                                <td>${tag.name}</td>
                                <td>${tag.slug}</td>
                                <td>
                                    <fmt:parseDate value="${tag.createdAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                    <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm" />
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary" 
                                                data-bs-toggle="modal" data-bs-target="#editTagModal"
                                                data-id="${tag.id}"
                                                data-name="${tag.name}"
                                                data-slug="${tag.slug}">
                                            <i class="bi bi-pencil"></i> Sửa
                                        </button>
                                        <button type="button" class="btn btn-sm btn-danger ms-1" 
                                                data-bs-toggle="modal" data-bs-target="#deleteTagModal"
                                                data-id="${tag.id}"
                                                data-name="${tag.name}">
                                            <i class="bi bi-trash"></i> Xóa
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </main>
    </div>
</div>

<!-- Modal thêm thẻ -->
<div class="modal fade" id="addTagModal" tabindex="-1" aria-labelledby="addTagModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addTagModalLabel">Thêm thẻ mới</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/tags" method="post">
                <div class="modal-body">
                    <input type="hidden" name="action" value="add">
                    <div class="mb-3">
                        <label for="tagName" class="form-label">Tên thẻ</label>
                        <input type="text" class="form-control" id="tagName" name="name" required>
                    </div>
                    <div class="mb-3">
                        <label for="tagSlug" class="form-label">Slug (để trống sẽ tự động tạo)</label>
                        <input type="text" class="form-control" id="tagSlug" name="slug">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal sửa thẻ -->
<div class="modal fade" id="editTagModal" tabindex="-1" aria-labelledby="editTagModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="editTagModalLabel">Sửa thẻ</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/tags" method="post">
                <div class="modal-body">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" id="editTagId" name="id">
                    <div class="mb-3">
                        <label for="editTagName" class="form-label">Tên thẻ</label>
                        <input type="text" class="form-control" id="editTagName" name="name" required>
                    </div>
                    <div class="mb-3">
                        <label for="editTagSlug" class="form-label">Slug</label>
                        <input type="text" class="form-control" id="editTagSlug" name="slug">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Cập nhật</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal xóa thẻ -->
<div class="modal fade" id="deleteTagModal" tabindex="-1" aria-labelledby="deleteTagModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="deleteTagModalLabel">Xác nhận xóa</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Bạn có chắc chắn muốn xóa thẻ <strong id="deleteTagName"></strong>?</p>
                <p class="text-danger">Lưu ý: Việc này sẽ xóa thẻ khỏi tất cả bài viết liên quan.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <form action="${pageContext.request.contextPath}/admin/tags" method="post">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" id="deleteTagId" name="id">
                    <button type="submit" class="btn btn-danger">Xóa</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Script xử lý modal -->
<script>
document.addEventListener('DOMContentLoaded', function() {
    // Xử lý modal sửa thẻ
    const editTagModal = document.getElementById('editTagModal');
    if (editTagModal) {
        editTagModal.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-id');
            const name = button.getAttribute('data-name');
            const slug = button.getAttribute('data-slug');
            
            console.log("Edit modal - ID:", id, "Name:", name, "Slug:", slug);
            
            document.getElementById('editTagId').value = id;
            document.getElementById('editTagName').value = name;
            document.getElementById('editTagSlug').value = slug;
        });
    }
    
    // Xử lý modal xóa thẻ
    const deleteTagModal = document.getElementById('deleteTagModal');
    if (deleteTagModal) {
        deleteTagModal.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-id');
            const name = button.getAttribute('data-name');
            
            document.getElementById('deleteTagId').value = id;
            document.getElementById('deleteTagName').textContent = name;
        });
    }
});
</script>

<%@ include file="/menu/footer.jsp" %>
</body>
</html> 