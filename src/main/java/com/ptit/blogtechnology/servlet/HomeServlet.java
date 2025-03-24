package com.ptit.blogtechnology.servlet;

import com.ptit.blogtechnology.dao.PostDAO;
import com.ptit.blogtechnology.dao.CategoryDAO;
import com.ptit.blogtechnology.model.Post;
import com.ptit.blogtechnology.model.Category;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("")
public class HomeServlet extends HttpServlet {
  private PostDAO postDAO;
  private CategoryDAO categoryDAO;

  @Override
  public void init() throws ServletException {
    postDAO = new PostDAO();
    categoryDAO = new CategoryDAO();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Lấy tham số phân trang
    int page = 1;
    int postsPerPage = 10;

    try {
      if (request.getParameter("page") != null) {
        page = Integer.parseInt(request.getParameter("page"));
      }
    } catch (NumberFormatException e) {
      page = 1;
    }

    // Lấy danh sách bài viết đã xuất bản với phân trang
    List<Post> posts = postDAO.findPublishedPosts(page, postsPerPage);

    // Lấy tổng số bài viết để tính số trang
    int totalPosts = postDAO.countPublishedPosts();
    int totalPages = (int) Math.ceil((double) totalPosts / postsPerPage);

    // Lấy bài viết nổi bật cho carousel
    List<Post> featuredPosts = postDAO.findFeaturedPosts(5);

    // Lấy danh sách danh mục
    List<Category> categories = categoryDAO.findAll();

    // Kiểm tra thông báo đăng ký nhận tin
    String subscribeStatus = request.getParameter("subscribe");
    if (subscribeStatus != null) {
      if (subscribeStatus.equals("success")) {
        request.setAttribute("subscribeMessage", "Đăng ký nhận tin thành công!");
        request.setAttribute("subscribeMessageType", "success");
      } else if (subscribeStatus.equals("error")) {
        String errorMessage = request.getParameter("message");
        if (errorMessage != null) {
          if (errorMessage.equals("empty_email")) {
            request.setAttribute("subscribeMessage", "Vui lòng nhập địa chỉ email!");
          } else if (errorMessage.equals("email_exists")) {
            request.setAttribute("subscribeMessage", "Email này đã đăng ký nhận tin!");
          } else {
            request.setAttribute("subscribeMessage", "Đã xảy ra lỗi khi đăng ký nhận tin!");
          }
          request.setAttribute("subscribeMessageType", "danger");
        }
      }
    }

    // Đặt các thuộc tính vào request
    request.setAttribute("posts", posts);
    request.setAttribute("featuredPosts", featuredPosts);
    request.setAttribute("categories", categories);
    request.setAttribute("currentPage", page);
    request.setAttribute("totalPages", totalPages);

    // Chuyển hướng đến trang chủ
    request.getRequestDispatcher("/index.jsp").forward(request, response);
  }
}