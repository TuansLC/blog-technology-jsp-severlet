package com.ptit.blogtechnology.servlet;

import com.ptit.blogtechnology.dao.ContactDAO;
import com.ptit.blogtechnology.model.Contact;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/contact-us")
public class ContactServlet extends HttpServlet {
    private ContactDAO contactDAO;

    @Override
    public void init() throws ServletException {
        contactDAO = new ContactDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/contact.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Lấy thông tin từ form
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message = request.getParameter("message");

        // Tạo đối tượng Contact
        Contact contact = new Contact();
        contact.setName(name);
        contact.setEmail(email);
        contact.setMessage(message);

        // Lưu vào database
        if (contactDAO.save(contact)) {
            request.setAttribute("message", "Cảm ơn bạn đã liên hệ. Chúng tôi sẽ phản hồi sớm nhất có thể!");
            request.setAttribute("messageType", "success");
        } else {
            request.setAttribute("message", "Có lỗi xảy ra. Vui lòng thử lại sau!");
            request.setAttribute("messageType", "danger");
        }

        // Hiển thị lại trang contact với thông báo
        request.getRequestDispatcher("/contact.jsp").forward(request, response);
    }
} 