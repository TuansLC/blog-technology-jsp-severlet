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
import java.util.logging.Logger;

@WebFilter(urlPatterns = {"/profile", "/create-post", "/edit-post/*", "/delete-post/*"})
public class AuthenticationFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());

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
        boolean isLoggedIn = (session != null && session.getAttribute("currentUser") != null);

        LOGGER.info("AuthenticationFilter checking: " + httpRequest.getRequestURI() + 
                    ", isLoggedIn: " + isLoggedIn);

        if (isLoggedIn) {
            // Người dùng đã đăng nhập, cho phép tiếp tục
            chain.doFilter(request, response);
        } else {
            // Người dùng chưa đăng nhập, chuyển hướng đến trang đăng nhập
            String loginPath = httpRequest.getContextPath() + "/sign-in";
            LOGGER.info("Redirecting to: " + loginPath);
            httpResponse.sendRedirect(loginPath);
        }
    }

    @Override
    public void destroy() {
        // Không cần xử lý gì
    }
}
