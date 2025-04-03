package com.ptit.blogtechnology.filter;

import com.ptit.blogtechnology.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

public class AdminFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(AdminFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Lấy thông tin người dùng từ session
        HttpSession session = httpRequest.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        
        // Kiểm tra quyền truy cập
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            // Chuyển hướng đến trang đăng nhập
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/sign-in");
            return;
        }
        
        // Cho phép request tiếp tục
        chain.doFilter(request, response);
    }
} 