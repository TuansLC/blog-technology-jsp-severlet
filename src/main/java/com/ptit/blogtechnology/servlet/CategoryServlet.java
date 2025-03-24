package com.ptit.blogtechnology.servlet;

import com.ptit.blogtechnology.dao.CategoryDAO;
import com.ptit.blogtechnology.dao.PostDAO;
import com.ptit.blogtechnology.model.Category;
import com.ptit.blogtechnology.model.Post;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "CategoryServlet", urlPatterns = {"/category/*"})
public class CategoryServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger(CategoryServlet.class.getName());
  private CategoryDAO categoryDAO;
  private PostDAO postDAO;
  private static final int POSTS_PER_PAGE = 10;

  @Override
  public void init() throws ServletException {
    super.init();
    categoryDAO = new CategoryDAO();
    postDAO = new PostDAO();
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

    // Tìm danh mục theo slug
    Category category = categoryDAO.findBySlug(slug);

    // Nếu không tìm thấy danh mục, chuyển hướng về trang chủ
    if (category == null) {
      response.sendRedirect(request.getContextPath() + "/");
      return;
    }

    // Lấy trang hiện tại
    int page = 1;
    String pageParam = request.getParameter("page");
    if (pageParam != null && !pageParam.isEmpty()) {
      try {
        page = Integer.parseInt(pageParam);
        if (page < 1) {
          page = 1;
        }
      } catch (NumberFormatException e) {
        LOGGER.log(Level.WARNING, "Lỗi khi chuyển đổi tham số trang", e);
      }
    }

    // Lấy danh sách bài viết theo danh mục
    List<Post> posts = postDAO.findByCategory(category.getId(), page, POSTS_PER_PAGE);

    // Đếm tổng số bài viết theo danh mục
    int totalPosts = postDAO.countByCategory(category.getId());

    // Tính tổng số trang
    int totalPages = (int) Math.ceil((double) totalPosts / POSTS_PER_PAGE);

    // Đặt thuộc tính cho request
    request.setAttribute("category", category);
    request.setAttribute("posts", posts);
    request.setAttribute("currentPage", page);
    request.setAttribute("totalPages", totalPages);
    request.setAttribute("totalPosts", totalPosts);

    // Lấy danh sách tất cả danh mục để hiển thị ở sidebar
    List<Category> categories = categoryDAO.findAll();
    request.setAttribute("categories", categories);

    // Chuyển hướng đến trang danh mục
    request.getRequestDispatcher("/category.jsp").forward(request, response);
  }
}