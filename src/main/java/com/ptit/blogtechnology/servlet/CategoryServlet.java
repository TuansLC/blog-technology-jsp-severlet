package com.ptit.blogtechnology.servlet;

import com.ptit.blogtechnology.dao.CategoryDAO;
import com.ptit.blogtechnology.dao.PostDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/category")
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
    // Xử lý hiển thị trang danh mục
    request.getRequestDispatcher("/category.jsp").forward(request, response);
  }
}