package com.ptit.blogtechnology.servlet;

import com.ptit.blogtechnology.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/sign-out")
public class SignOutServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(SignOutServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            LOGGER.info("User logging out: " + 
                ((User)session.getAttribute("currentUser")).getUsername());
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/");
    }
} 