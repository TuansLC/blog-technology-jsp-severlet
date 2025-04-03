<form action="${pageContext.request.contextPath}/admin/post-form" method="post">
    <input type="hidden" name="mode" value="${mode}">
    <c:if test="${mode eq 'edit'}">
        <input type="hidden" name="id" value="${post.id}">
    </c:if>
    
    <!-- Các trường khác -->
    <div class="mb-3">
        <label for="title" class="form-label">Tiêu đề</label>
        <input type="text" class="form-control" id="title" name="title" value="${post.title}" required>
    </div>
    
    <div class="mb-3">
        <label for="slug" class="form-label">Slug</label>
        <input type="text" class="form-control" id="slug" name="slug" value="${post.slug}">
        <small class="text-muted">Để trống để tự động tạo từ tiêu đề</small>
    </div>
    
    <!-- Các trường khác -->
    
    <div class="mb-3">
        <label for="categories" class="form-label">Danh mục</label>
        <select class="form-select" id="categories" name="categories" multiple>
            <c:forEach var="category" items="${categories}">
                <option value="${category.id}" 
                    <c:if test="${postCategories.contains(category)}">selected</c:if>>
                    ${category.name}
                </option>
            </c:forEach>
        </select>
    </div>
    
    <div class="mb-3">
        <label for="tags" class="form-label">Thẻ</label>
        <select class="form-select" id="tags" name="tags" multiple>
            <c:forEach var="tag" items="${tags}">
                <option value="${tag.id}" 
                    <c:if test="${postTags.contains(tag)}">selected</c:if>>
                    ${tag.name}
                </option>
            </c:forEach>
        </select>
    </div>
    
    <!-- Nút submit -->
    <button type="submit" class="btn btn-primary">Lưu</button>
</form> 