package com.ptit.blogtechnology.servlet;

import com.ptit.blogtechnology.dao.UserDAO;
import com.ptit.blogtechnology.model.User;
import com.ptit.blogtechnology.utils.PasswordHasher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/sign-up")
public class SignUpServlet extends HttpServlet {
  private UserDAO userDAO;

  @Override
  public void init() throws ServletException {
    userDAO = new UserDAO();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    request.getRequestDispatcher("/sign-up.jsp").forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Lấy thông tin từ form
    String username = request.getParameter("userName");
    String fullName = request.getParameter("fullName");
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    String confirmPassword = request.getParameter("confirmPassword");

    // Kiểm tra xác nhận mật khẩu
    if (!password.equals(confirmPassword)) {
      request.setAttribute("error", "Mật khẩu xác nhận không khớp");
      request.getRequestDispatcher("/sign-up.jsp").forward(request, response);
      return;
    }

    // Kiểm tra username đã tồn tại chưa
    if (userDAO.findByUsername(username) != null) {
      request.setAttribute("error", "Tên đăng nhập đã tồn tại");
      request.getRequestDispatcher("/sign-up.jsp").forward(request, response);
      return;
    }

    // Kiểm tra email đã tồn tại chưa
    if (userDAO.findByEmail(email) != null) {
      request.setAttribute("error", "Email đã được sử dụng");
      request.getRequestDispatcher("/sign-up.jsp").forward(request, response);
      return;
    }

    // Mã hóa mật khẩu
    String passwordHash = PasswordHasher.hashPassword(password);

    // Tạo đối tượng User mới
    User newUser = new User();
    newUser.setUsername(username);
    newUser.setFullName(fullName);
    newUser.setEmail(email);
    newUser.setPasswordHash(passwordHash);
    newUser.setCreatedAt(LocalDateTime.now());
    newUser.setUpdatedAt(LocalDateTime.now());

    boolean success = userDAO.save(newUser);

    System.out.println(email + "Registration successful!");

    if (success) {
      request.getSession().setAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");
      response.sendRedirect(request.getContextPath() + "/sign-in");
    } else {
      request.setAttribute("error", "Đã xảy ra lỗi khi đăng ký. Vui lòng thử lại.");
      request.getRequestDispatcher("/sign-up.jsp").forward(request, response);
    }
  }
}
