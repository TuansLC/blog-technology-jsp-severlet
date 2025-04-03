package com.ptit.blogtechnology.servlet.admin;

import com.ptit.blogtechnology.dao.CategoryDAO;
import com.ptit.blogtechnology.model.Category;
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

@WebServlet("/admin/categories")
public class AdminCategoriesServlet extends HttpServlet {
    private CategoryDAO categoryDAO;
    private static final Logger LOGGER = Logger.getLogger(AdminCategoriesServlet.class.getName());

    @Override
    public void init() throws ServletException {
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        LOGGER.info("AdminCategoriesServlet.doGet called");
        
        List<Category> categories = categoryDAO.findAll();
        request.setAttribute("categories", categories);
        
        request.getRequestDispatcher("/admin/categories.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        LOGGER.info("AdminCategoriesServlet.doPost called with action: " + action);
        
        try {
            if ("add".equals(action)) {
                addCategory(request, response);
            } else if ("edit".equals(action)) {
                editCategory(request, response);
            } else if ("delete".equals(action)) {
                deleteCategory(request, response);
            } else {
                LOGGER.warning("Unknown action: " + action);
                response.sendRedirect(request.getContextPath() + "/admin/categories");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in doPost", e);
            request.getSession().setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/categories");
        }
    }
    
    private void addCategory(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String name = request.getParameter("name");
            String slug = request.getParameter("slug");
            String description = request.getParameter("description");
            
            LOGGER.info("Adding category - Name: " + name + ", Slug: " + slug);
            
            // Tạo slug tự động nếu không có
            if (slug == null || slug.trim().isEmpty()) {
                slug = SlugGenerator.toSlug(name);
                LOGGER.info("Generated slug: " + slug);
            }
            
            Category category = new Category();
            category.setName(name);
            category.setSlug(slug);
            category.setDescription(description);
            category.setCreatedAt(LocalDateTime.now());
            category.setUpdatedAt(LocalDateTime.now());
            
            boolean success = categoryDAO.save(category);
            
            if (success) {
                LOGGER.info("Thêm danh mục thành công: " + name);
                request.getSession().setAttribute("successMessage", "Thêm danh mục thành công!");
            } else {
                LOGGER.warning("Thêm danh mục thất bại: " + name);
                request.getSession().setAttribute("errorMessage", "Thêm danh mục thất bại!");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in addCategory", e);
            throw e; // Rethrow để có thể xem stack trace đầy đủ
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/categories");
    }
    
    private void editCategory(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            String slug = request.getParameter("slug");
            String description = request.getParameter("description");
            
            // Thêm log để debug
            LOGGER.info("Editing category - ID: " + id + ", Name: " + name + ", Slug: " + slug);
            
            // Tạo slug tự động nếu không có
            if (slug == null || slug.trim().isEmpty()) {
                slug = SlugGenerator.toSlug(name);
                LOGGER.info("Generated slug: " + slug);
            }
            
            Category category = categoryDAO.findById(id);
            if (category != null) {
                category.setName(name);
                category.setSlug(slug);
                category.setDescription(description);
                category.setUpdatedAt(LocalDateTime.now());
                
                boolean success = categoryDAO.update(category);
                
                if (success) {
                    LOGGER.info("Cập nhật danh mục thành công: " + name);
                    request.getSession().setAttribute("successMessage", "Cập nhật danh mục thành công!");
                } else {
                    LOGGER.warning("Cập nhật danh mục thất bại: " + name);
                    request.getSession().setAttribute("errorMessage", "Cập nhật danh mục thất bại!");
                }
            } else {
                LOGGER.warning("Không tìm thấy danh mục với ID: " + id);
                request.getSession().setAttribute("errorMessage", "Không tìm thấy danh mục!");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật danh mục", e);
            request.getSession().setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            throw e; // Thêm dòng này để xem stack trace đầy đủ
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/categories");
    }
    
    private void deleteCategory(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            
            boolean success = categoryDAO.delete(id);
            
            if (success) {
                LOGGER.info("Xóa danh mục thành công với ID: " + id);
                request.getSession().setAttribute("successMessage", "Xóa danh mục thành công!");
            } else {
                LOGGER.warning("Xóa danh mục thất bại với ID: " + id);
                request.getSession().setAttribute("errorMessage", "Xóa danh mục thất bại!");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa danh mục", e);
            request.getSession().setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/categories");
    }
} 