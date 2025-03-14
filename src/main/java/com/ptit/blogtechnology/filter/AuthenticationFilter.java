package com.ptit.blogtechnology.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/admin/*", "/profile", "/create-post", "/edit-post/*", "/delete-post/*"})
public class AuthenticationFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // Không cần xử lý gì
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    HttpSession session = httpRequest.getSession(false);
    boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

    if (isLoggedIn) {
      // Người dùng đã đăng nhập, cho phép tiếp tục
      chain.doFilter(request, response);
    } else {
      // Người dùng chưa đăng nhập, chuyển hướng đến trang đăng nhập
      httpResponse.sendRedirect(httpRequest.getContextPath() + "/sign-in");
    }
  }

  @Override
  public void destroy() {
    // Không cần xử lý gì
  }
}
