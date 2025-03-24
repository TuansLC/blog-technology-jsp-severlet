<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${post.title} - Blog Công nghệ</title>
    <meta name="description" content="${post.summary}">
    <meta property="og:title" content="${post.title}">
    <meta property="og:description" content="${post.summary}">
    <meta property="og:image" content="${post.featuredImage}">
    <meta property="og:url" content="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/post/${post.slug}">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
    <!-- Thêm CSS cho highlight.js nếu cần hiển thị code -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.7.0/styles/github.min.css">
</head>
<body>
<jsp:include page="/menu/header.jsp" />

<div class="container mt-4">
    <div class="row">
        <div class="col-lg-8">
            <!-- Bài viết chính -->
            <article class="blog-post">
                <h1 class="blog-post-title">${post.title}</h1>

                <div class="blog-post-meta mb-3">
                    <span class="text-muted">
                        <i class="bi bi-calendar"></i>
                        <fmt:parseDate value="${post.publishedAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                        <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm" />
                    </span>
                    <span class="text-muted ms-3">
                        <i class="bi bi-person"></i> ${post.author.fullName}
                    </span>
                    <span class="text-muted ms-3">
                        <i class="bi bi-eye"></i> ${post.viewCount} lượt xem
                    </span>
                </div>

                <div class="categories mb-3">
                    <i class="bi bi-folder"></i>
                    <c:forEach var="category" items="${post.categories}" varStatus="loop">
                        <a href="${pageContext.request.contextPath}/category/${category.slug}" class="badge bg-secondary text-decoration-none">${category.name}</a>
                        <c:if test="${!loop.last}">&nbsp;</c:if>
                    </c:forEach>
                </div>

                <c:if test="${not empty post.tags}">
                    <div class="tags mb-3">
                        <i class="bi bi-tags"></i>
                        <c:forEach var="tag" items="${post.tags}" varStatus="loop">
                            <a href="${pageContext.request.contextPath}/tag/${tag.slug}" class="badge bg-info text-decoration-none">${tag.name}</a>
                            <c:if test="${!loop.last}">&nbsp;</c:if>
                        </c:forEach>
                    </div>
                </c:if>

                <c:if test="${not empty post.featuredImage}">
                    <div class="featured-image mb-4">
                        <img src="${post.featuredImage}" alt="${post.title}" class="img-fluid rounded">
                    </div>
                </c:if>

                <div class="blog-post-content">
                    ${post.content}
                </div>

                <!-- Chia sẻ bài viết -->
                <div class="share-buttons mt-4">
                    <h5>Chia sẻ bài viết:</h5>
                    <a href="https://www.facebook.com/sharer/sharer.php?u=${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/post/${post.slug}"
                       target="_blank" class="btn btn-primary">
                        <i class="bi bi-facebook"></i> Facebook
                    </a>
                    <a href="https://twitter.com/intent/tweet?url=${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/post/${post.slug}&text=${post.title}"
                       target="_blank" class="btn btn-info text-white">
                        <i class="bi bi-twitter"></i> Twitter
                    </a>
                    <a href="https://www.linkedin.com/shareArticle?mini=true&url=${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/post/${post.slug}"
                       target="_blank" class="btn btn-secondary">
                        <i class="bi bi-linkedin"></i> LinkedIn
                    </a>
                </div>
            </article>

            <!-- Bài viết liên quan -->
            <c:if test="${not empty relatedPosts}">
                <div class="related-posts mt-5">
                    <h3>Bài viết liên quan</h3>
                    <div class="row">
                        <c:forEach var="relatedPost" items="${relatedPosts}">
                            <div class="col-md-4 mb-3">
                                <div class="card h-100">
                                    <c:if test="${not empty relatedPost.featuredImage}">
                                        <img src="${relatedPost.featuredImage}" class="card-img-top" alt="${relatedPost.title}">
                                    </c:if>
                                    <div class="card-body">
                                        <h5 class="card-title">
                                            <a href="${pageContext.request.contextPath}/post/${relatedPost.slug}" class="text-decoration-none">${relatedPost.title}</a>
                                        </h5>
                                        <p class="card-text small text-muted">
                                            <fmt:parseDate value="${relatedPost.publishedAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                            <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy" />
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>

            <!-- Phần bình luận -->
            <div class="comments-section mt-5">
                <h3>Bình luận (${comments.size()})</h3>

                <!-- Thông báo bình luận -->
                <c:if test="${not empty commentMessage}">
                    <div class="alert alert-${commentMessageType} alert-dismissible fade show" role="alert">
                            ${commentMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <!-- Form bình luận -->
                <div class="card mb-4">
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/post" method="post" id="commentForm">
                            <input type="hidden" name="postId" value="${post.id}">
                            <input type="hidden" name="parentId" id="parentId" value="">

                            <div class="mb-3">
                                <label for="content" class="form-label">Bình luận của bạn</label>
                                <textarea class="form-control" id="content" name="content" rows="3" required></textarea>
                            </div>

                            <c:choose>
                                <c:when test="${empty sessionScope.currentUser}">
                                    <!-- Nếu chưa đăng nhập, hiển thị form nhập thông tin -->
                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label for="authorName" class="form-label">Tên của bạn</label>
                                            <input type="text" class="form-control" id="authorName" name="authorName" required>
                                        </div>
                                        <div class="col-md-6 mb-3">
                                            <label for="authorEmail" class="form-label">Email của bạn</label>
                                            <input type="email" class="form-control" id="authorEmail" name="authorEmail" required>
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <!-- Nếu đã đăng nhập, hiển thị thông tin người dùng -->
                                    <div class="mb-3">
                                        <p class="text-muted">Bình luận với tư cách: <strong>${sessionScope.currentUser.fullName}</strong></p>
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <button type="submit" class="btn btn-primary">Gửi bình luận</button>
                            <button type="button" id="cancelReply" class="btn btn-secondary d-none">Hủy trả lời</button>
                        </form>
                    </div>
                </div>

                <!-- Danh sách bình luận -->
                <div class="comments-list">
                    <c:choose>
                        <c:when test="${empty comments}">
                            <p class="text-muted">Chưa có bình luận nào. Hãy là người đầu tiên bình luận!</p>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="comment" items="${comments}">
                                <div class="comment card mb-3" id="comment-${comment.id}">
                                    <div class="card-body">
                                        <div class="d-flex">
                                            <div class="flex-shrink-0">
                                                <img src="https://via.placeholder.com/50" class="rounded-circle" alt="Avatar">
                                            </div>
                                            <div class="flex-grow-1 ms-3">
                                                <div class="d-flex justify-content-between">
                                                    <h5 class="mt-0">${comment.displayName}</h5>
                                                    <small class="text-muted">${comment.formattedCreatedAt}</small>
                                                </div>
                                                <p>${comment.content}</p>
                                                <button class="btn btn-sm btn-link reply-btn" data-comment-id="${comment.id}">Trả lời</button>

                                                <!-- Bình luận trả lời -->
                                                <c:if test="${comment.hasReplies()}">
                                                    <div class="replies mt-3">
                                                        <c:forEach var="reply" items="${comment.replies}">
                                                            <div class="reply d-flex mt-3" id="comment-${reply.id}">
                                                                <div class="flex-shrink-0">
                                                                    <img src="https://via.placeholder.com/40" class="rounded-circle" alt="Avatar">
                                                                </div>
                                                                <div class="flex-grow-1 ms-3">
                                                                    <div class="d-flex justify-content-between">
                                                                        <h6 class="mt-0">${reply.displayName}</h6>
                                                                        <small class="text-muted">${reply.formattedCreatedAt}</small>
                                                                    </div>
                                                                    <p>${reply.content}</p>
                                                                    <button class="btn btn-sm btn-link reply-btn" data-comment-id="${comment.id}">Trả lời</button>
                                                                </div>
                                                            </div>
                                                        </c:forEach>
                                                    </div>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Sidebar -->
        <div class="col-lg-4">
            <div class="position-sticky" style="top: 2rem;">
                <!-- Về tác giả -->
                <div class="card mb-4">
                    <div class="card-header">Về tác giả</div>
                    <div class="card-body">
                        <div class="d-flex align-items-center mb-3">
                            <img src="https://via.placeholder.com/60" class="rounded-circle me-3" alt="${post.author.fullName}">
                            <div>
                                <h5 class="mb-0">${post.author.fullName}</h5>
                                <p class="text-muted mb-0">@${post.author.username}</p>
                            </div>
                        </div>
                        <p>Tác giả của nhiều bài viết về công nghệ và lập trình.</p>
                        <a href="${pageContext.request.contextPath}/author/${post.author.username}" class="btn btn-outline-primary btn-sm">Xem tất cả bài viết</a>
                    </div>
                </div>

                <!-- Bài viết phổ biến -->
                <div class="card mb-4">
                    <div class="card-header">Bài viết phổ biến</div>
                    <div class="card-body">
                        <ul class="list-unstyled mb-0">
                            <c:forEach var="popularPost" items="${popularPosts}">
                                <li class="mb-2">
                                    <a href="${pageContext.request.contextPath}/post/${popularPost.slug}" class="text-decoration-none">
                                            ${popularPost.title}
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>

                <!-- Danh mục -->
                <div class="card mb-4">
                    <div class="card-header">Danh mục</div>
                    <div class="card-body">
                        <ul class="list-unstyled mb-0">
                            <c:forEach var="category" items="${categories}">
                                <li class="mb-2">
                                    <a href="${pageContext.request.contextPath}/category/${category.slug}" class="text-decoration-none">
                                            ${category.name}
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>

                <!-- Tags -->
                <div class="card mb-4">
                    <div class="card-header">Tags</div>
                    <div class="card-body">
                        <div class="d-flex flex-wrap gap-2">
                            <c:forEach var="tag" items="${tags}">
                                <a href="${pageContext.request.contextPath}/tag/${tag.slug}" class="badge bg-secondary text-decoration-none">
                                        ${tag.name}
                                </a>
                            </c:forEach>
                        </div>
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
<!-- Highlight.js cho hiển thị code -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.7.0/highlight.min.js"></script>
<script>
  // Khởi tạo highlight.js
  document.addEventListener('DOMContentLoaded', (event) => {
    document.querySelectorAll('pre code').forEach((el) => {
      hljs.highlightElement(el);
    });

    // Xử lý nút trả lời bình luận
    const replyButtons = document.querySelectorAll('.reply-btn');
    const commentForm = document.getElementById('commentForm');
    const parentIdInput = document.getElementById('parentId');
    const cancelReplyButton = document.getElementById('cancelReply');

    replyButtons.forEach(button => {
      button.addEventListener('click', function() {
        const commentId = this.getAttribute('data-comment-id');
        parentIdInput.value = commentId;

        // Di chuyển form đến sau bình luận được trả lời
        const commentElement = document.getElementById('comment-' + commentId);
        commentElement.after(commentForm.closest('.card'));

        // Hiển thị nút hủy trả lời
        cancelReplyButton.classList.remove('d-none');

        // Thay đổi tiêu đề form
        const formLabel = commentForm.querySelector('label[for="content"]');
        formLabel.textContent = 'Trả lời bình luận';

        // Focus vào textarea
        document.getElementById('content').focus();
      });
    });

    // Xử lý nút hủy trả lời
    cancelReplyButton.addEventListener('click', function() {
      // Đặt lại giá trị parentId
      parentIdInput.value = '';

      // Di chuyển form về vị trí ban đầu
      const commentsSection = document.querySelector('.comments-section');
      const commentsList = document.querySelector('.comments-list');
      commentsSection.insertBefore(commentForm.closest('.card'), commentsList);

      // Ẩn nút hủy trả lời
      this.classList.add('d-none');

      // Đặt lại tiêu đề form
      const formLabel = commentForm.querySelector('label[for="content"]');
      formLabel.textContent = 'Bình luận của bạn';
    });
  });
</script>
</body>
</html>