package com.ptit.blogtechnology.servlet;

import com.ptit.blogtechnology.dao.UserDAO;
import com.ptit.blogtechnology.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/sign-in")
public class SignInServlet extends HttpServlet {
  private UserDAO userDAO;
  private static final Logger LOGGER = Logger.getLogger(SignInServlet.class.getName());

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
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    
    User user = userDAO.findByEmail(email);
    LOGGER.info("Login attempt for: " + email);

    if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
        LOGGER.info("Login successful - User: " + user.getUsername() + ", Role: " + user.getRole());
        
        HttpSession session = request.getSession();
        session.setAttribute("currentUser", user);
        
        // Kiểm tra và xử lý redirect URL
        String redirectUrl = (String) session.getAttribute("redirectUrl");
        if (redirectUrl != null && "ADMIN".equals(user.getRole())) {
            session.removeAttribute("redirectUrl");
            LOGGER.info("Redirecting to saved URL: " + redirectUrl);
            response.sendRedirect(redirectUrl);
        } else {
            LOGGER.info("Redirecting to home page");
            response.sendRedirect(request.getContextPath() + "/");
        }
    } else {
        LOGGER.warning("Login failed for: " + email);
        request.setAttribute("error", "Email hoặc mật khẩu không đúng");
        request.getRequestDispatcher("/sign-in.jsp").forward(request, response);
    }
  }
}
