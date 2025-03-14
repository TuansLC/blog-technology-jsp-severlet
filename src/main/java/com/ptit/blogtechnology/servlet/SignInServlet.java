package com.ptit.blogtechnology.servlet;

import com.ptit.blogtechnology.dao.UserDAO;
import com.ptit.blogtechnology.model.User;

import com.ptit.blogtechnology.utils.PasswordHasher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/sign-in")
public class SignInServlet extends HttpServlet {
  private UserDAO userDAO;

  @Override
  public void init() throws ServletException {
    userDAO = new UserDAO();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    request.getRequestDispatcher("/sign-in.jsp").forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Lấy thông tin từ form
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    boolean rememberMe = request.getParameter("rememberMe") != null;

    // Tìm user theo email
    User user = userDAO.findByEmail(email);

    // Kiểm tra user tồn tại và mật khẩu đúng
    if (user != null && PasswordHasher.checkPassword(password, user.getPasswordHash())) {
      // Tạo session
      HttpSession session = request.getSession();
      session.setAttribute("user", user);

      // Xử lý "Remember Me"
      if (rememberMe) {
        // Thời gian session tồn tại (7 ngày)
        session.setMaxInactiveInterval(7 * 24 * 60 * 60);
      }
      System.out.println( user.getEmail() + "Login Successfully!");
      // Chuyển hướng đến trang chủ
      response.sendRedirect(request.getContextPath() + "/");
    } else {
      // Thông báo lỗi
      request.setAttribute("error", "Email hoặc mật khẩu không đúng");
      request.getRequestDispatcher("/sign-in.jsp").forward(request, response);
    }
  }
}
