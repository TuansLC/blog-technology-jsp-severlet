package com.ptit.blogtechnology.servlet;

import com.ptit.blogtechnology.dao.CommentDAO;
import com.ptit.blogtechnology.dao.PostDAO;
import com.ptit.blogtechnology.model.Comment;
import com.ptit.blogtechnology.model.Post;
import com.ptit.blogtechnology.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "PostDetailServlet", urlPatterns = {"/post/*"})
public class PostDetailServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger(PostDetailServlet.class.getName());
  private PostDAO postDAO;
  private CommentDAO commentDAO;

  @Override
  public void init() throws ServletException {
    super.init();
    postDAO = new PostDAO();
    commentDAO = new CommentDAO();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String pathInfo = request.getPathInfo();

    // Kiểm tra đường dẫn
    if (pathInfo == null || pathInfo.equals("/")) {
      response.sendRedirect(request.getContextPath() + "/");
      return;
    }

    // Lấy slug từ đường dẫn (bỏ dấu / đầu tiên)
    String slug = pathInfo.substring(1);

    // Tìm bài viết theo slug
    Post post = postDAO.findBySlug(slug);

    // Nếu không tìm thấy bài viết, chuyển hướng về trang chủ
    if (post == null) {
      response.sendRedirect(request.getContextPath() + "/");
      return;
    }

    // Tăng lượt xem bài viết
    postDAO.incrementViewCount(post.getId());

    // Lấy danh sách bình luận
    List<Comment> comments = commentDAO.findByPostId(post.getId());

    // Lấy bài viết liên quan
    List<Post> relatedPosts = postDAO.findRelatedPosts(post.getId(), 3);

    // Đặt thuộc tính cho request
    request.setAttribute("post", post);
    request.setAttribute("comments", comments);
    request.setAttribute("relatedPosts", relatedPosts);

    // Kiểm tra thông báo từ session (nếu có)
    HttpSession session = request.getSession();
    String commentMessage = (String) session.getAttribute("commentMessage");
    String commentMessageType = (String) session.getAttribute("commentMessageType");

    if (commentMessage != null) {
      request.setAttribute("commentMessage", commentMessage);
      request.setAttribute("commentMessageType", commentMessageType);
      session.removeAttribute("commentMessage");
      session.removeAttribute("commentMessageType");
    }

    // Chuyển hướng đến trang chi tiết bài viết
    request.getRequestDispatcher("/post-detail.jsp").forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");

    // Lấy thông tin từ form
    String postIdStr = request.getParameter("postId");
    String content = request.getParameter("content");
    String parentIdStr = request.getParameter("parentId");

    // Kiểm tra dữ liệu đầu vào
    if (postIdStr == null || content == null || content.trim().isEmpty()) {
      response.sendRedirect(request.getContextPath() + "/");
      return;
    }

    try {
      int postId = Integer.parseInt(postIdStr);
      Integer parentId = parentIdStr != null && !parentIdStr.isEmpty() ? Integer.parseInt(parentIdStr) : null;

      // Lấy thông tin người dùng từ session
      HttpSession session = request.getSession();
      User currentUser = (User) session.getAttribute("currentUser");

      // Tạo đối tượng Comment
      Comment comment = new Comment();
      comment.setPostId(postId);
      comment.setContent(content);
      comment.setParentId(parentId);
      comment.setStatus("NEW"); // Trạng thái mặc định

      // Nếu người dùng đã đăng nhập
      if (currentUser != null) {
        comment.setUserId(currentUser.getId().intValue());
      } else {
        // Nếu là khách, lấy thông tin từ form
        String authorName = request.getParameter("authorName");
        String authorEmail = request.getParameter("authorEmail");

        if (authorName == null || authorName.trim().isEmpty() ||
            authorEmail == null || authorEmail.trim().isEmpty()) {
          // Thiếu thông tin bắt buộc
          Post post = postDAO.findById(postId);
          if (post != null) {
            session.setAttribute("commentMessage", "Vui lòng nhập đầy đủ tên và email để bình luận.");
            session.setAttribute("commentMessageType", "danger");
            response.sendRedirect(request.getContextPath() + "/post/" + post.getSlug());
            return;
          } else {
            response.sendRedirect(request.getContextPath() + "/");
            return;
          }
        }

        comment.setAuthorName(authorName);
        comment.setAuthorEmail(authorEmail);
      }

      // Lưu bình luận
      boolean success = commentDAO.save(comment);

      // Lấy thông tin bài viết để chuyển hướng
      Post post = postDAO.findById(postId);

      if (post != null) {
        if (success) {
          session.setAttribute("commentMessage", "Bình luận của bạn đã được gửi thành công và đang chờ phê duyệt.");
          session.setAttribute("commentMessageType", "success");
        } else {
          session.setAttribute("commentMessage", "Có lỗi xảy ra khi gửi bình luận. Vui lòng thử lại sau.");
          session.setAttribute("commentMessageType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/post/" + post.getSlug());
      } else {
        response.sendRedirect(request.getContextPath() + "/");
      }

    } catch (NumberFormatException e) {
      LOGGER.log(Level.WARNING, "Lỗi khi chuyển đổi ID", e);
      response.sendRedirect(request.getContextPath() + "/");
    }
  }
}