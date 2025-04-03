package com.ptit.blogtechnology.servlet.admin;

import com.ptit.blogtechnology.dao.TagDAO;
import com.ptit.blogtechnology.model.Tag;
import com.ptit.blogtechnology.utils.SlugGenerator;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/tags")
public class AdminTagsServlet extends HttpServlet {
    private TagDAO tagDAO;
    private static final Logger LOGGER = Logger.getLogger(AdminTagsServlet.class.getName());

    @Override
    public void init() throws ServletException {
        tagDAO = new TagDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        LOGGER.info("AdminTagsServlet.doGet called");
        
        List<Tag> tags = tagDAO.findAll();
        request.setAttribute("tags", tags);
        
        request.getRequestDispatcher("/admin/tags.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        LOGGER.info("AdminTagsServlet.doPost called with action: " + action);
        
        try {
            if ("add".equals(action)) {
                addTag(request, response);
            } else if ("edit".equals(action)) {
                editTag(request, response);
            } else if ("delete".equals(action)) {
                deleteTag(request, response);
            } else {
                LOGGER.warning("Unknown action: " + action);
                response.sendRedirect(request.getContextPath() + "/admin/tags");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in doPost", e);
            request.getSession().setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/tags");
        }
    }
    
    private void addTag(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String name = request.getParameter("name");
            String slug = request.getParameter("slug");
            
            LOGGER.info("Adding tag - Name: " + name + ", Slug: " + slug);
            
            // Tạo slug tự động nếu không có
            if (slug == null || slug.trim().isEmpty()) {
                slug = SlugGenerator.toSlug(name);
                LOGGER.info("Generated slug: " + slug);
            }
            
            Tag tag = new Tag();
            tag.setName(name);
            tag.setSlug(slug);
            tag.setCreatedAt(LocalDateTime.now());
            tag.setUpdatedAt(LocalDateTime.now());
            
            boolean success = tagDAO.save(tag);
            
            if (success) {
                LOGGER.info("Thêm thẻ thành công: " + name);
                request.getSession().setAttribute("successMessage", "Thêm thẻ thành công!");
            } else {
                LOGGER.warning("Thêm thẻ thất bại: " + name);
                request.getSession().setAttribute("errorMessage", "Thêm thẻ thất bại!");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in addTag", e);
            throw e; // Rethrow để có thể xem stack trace đầy đủ
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/tags");
    }
    
    private void editTag(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            String slug = request.getParameter("slug");
            
            // Thêm log để debug
            LOGGER.info("Editing tag - ID: " + id + ", Name: " + name + ", Slug: " + slug);
            
            // Tạo slug tự động nếu không có
            if (slug == null || slug.trim().isEmpty()) {
                slug = SlugGenerator.toSlug(name);
                LOGGER.info("Generated slug: " + slug);
            }
            
            Tag tag = tagDAO.findById(id);
            if (tag != null) {
                tag.setName(name);
                tag.setSlug(slug);
                tag.setUpdatedAt(LocalDateTime.now());
                
                boolean success = tagDAO.update(tag);
                
                if (success) {
                    LOGGER.info("Cập nhật thẻ thành công: " + name);
                    request.getSession().setAttribute("successMessage", "Cập nhật thẻ thành công!");
                } else {
                    LOGGER.warning("Cập nhật thẻ thất bại: " + name);
                    request.getSession().setAttribute("errorMessage", "Cập nhật thẻ thất bại!");
                }
            } else {
                LOGGER.warning("Không tìm thấy thẻ với ID: " + id);
                request.getSession().setAttribute("errorMessage", "Không tìm thấy thẻ!");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in editTag", e);
            throw e; // Thêm dòng này để xem stack trace đầy đủ
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/tags");
    }
    
    private void deleteTag(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            
            LOGGER.info("Deleting tag with ID: " + id);
            
            boolean success = tagDAO.delete(id);
            
            if (success) {
                LOGGER.info("Xóa thẻ thành công với ID: " + id);
                request.getSession().setAttribute("successMessage", "Xóa thẻ thành công!");
            } else {
                LOGGER.warning("Xóa thẻ thất bại với ID: " + id);
                request.getSession().setAttribute("errorMessage", "Xóa thẻ thất bại!");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in deleteTag", e);
            throw e; // Thêm dòng này để xem stack trace đầy đủ
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/tags");
    }
} 