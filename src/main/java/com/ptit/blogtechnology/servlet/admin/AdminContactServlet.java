package com.ptit.blogtechnology.servlet.admin;

import com.ptit.blogtechnology.dao.ContactDAO;
import com.ptit.blogtechnology.model.Contact;
import com.ptit.blogtechnology.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/admin/contacts")
public class AdminContactServlet extends HttpServlet {
    private ContactDAO contactDAO;
    private static final Logger LOGGER = Logger.getLogger(AdminContactServlet.class.getName());
    private static final int CONTACTS_PER_PAGE = 10; // Số liên hệ trên mỗi trang

    @Override
    public void init() throws ServletException {
        contactDAO = new ContactDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        LOGGER.info("AdminContactServlet.doGet called");
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("currentUser");
            LOGGER.info("User in AdminContactServlet: " + 
                (user != null ? user.getUsername() + ", Role: " + user.getRole() : "null"));
        }
        
        // Lấy tham số lọc
        String status = request.getParameter("status");
        
        // Xử lý phân trang
        int page = 1;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            }
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid page parameter: " + request.getParameter("page"));
        }
        
        // Lấy tổng số liên hệ
        int totalContacts = contactDAO.countAllContacts(status);
        int totalPages = (int) Math.ceil((double) totalContacts / CONTACTS_PER_PAGE);
        
        // Lấy danh sách liên hệ cho trang hiện tại
        List<Contact> contacts = contactDAO.findAllPaginated(page, CONTACTS_PER_PAGE, status);
        LOGGER.info("Found " + contacts.size() + " contacts for page " + page + (status != null ? " with status " + status : ""));
        
        // Đặt các thuộc tính cho JSP
        request.setAttribute("contacts", contacts);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalContacts", totalContacts);
        request.setAttribute("status", status);
        
        request.getRequestDispatcher("/admin/contacts.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        int contactId = Integer.parseInt(request.getParameter("contactId"));
        
        if ("markAsRead".equals(action)) {
            contactDAO.updateStatus(contactId, Contact.Status.READ);
        } else if ("markAsReplied".equals(action)) {
            contactDAO.updateStatus(contactId, Contact.Status.REPLIED);
        }
        
        // Giữ lại trang hiện tại và trạng thái lọc sau khi cập nhật
        String page = request.getParameter("page");
        String status = request.getParameter("status");
        
        String redirectUrl = request.getContextPath() + "/admin/contacts";
        boolean hasParam = false;
        
        if (page != null && !page.isEmpty()) {
            redirectUrl += "?page=" + page;
            hasParam = true;
        }
        
        if (status != null && !status.isEmpty()) {
            redirectUrl += (hasParam ? "&" : "?") + "status=" + status;
        }
        
        response.sendRedirect(redirectUrl);
    }
} 