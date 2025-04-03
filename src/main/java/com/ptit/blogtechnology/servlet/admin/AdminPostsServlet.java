package com.ptit.blogtechnology.servlet.admin;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.ptit.blogtechnology.dao.CategoryDAO;
import com.ptit.blogtechnology.dao.PostDAO;
import com.ptit.blogtechnology.model.Category;
import com.ptit.blogtechnology.model.Post;
import com.ptit.blogtechnology.model.User;

@WebServlet("/admin/posts")
public class AdminPostsServlet extends HttpServlet {
    private PostDAO postDAO;
    private CategoryDAO categoryDAO;
    private static final Logger LOGGER = Logger.getLogger(AdminPostsServlet.class.getName());
    private static final int POSTS_PER_PAGE = 10;

    @Override
    public void init() throws ServletException {
        postDAO = new PostDAO();
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        LOGGER.info("AdminPostsServlet.doGet called");
        
        try {
            // Kiểm tra quyền truy cập
            User currentUser = (User) request.getSession().getAttribute("currentUser");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/sign-in");
                return;
            }
            
            // Lấy tham số lọc
            String status = request.getParameter("status");
            String categoryParam = request.getParameter("category");
            String search = request.getParameter("search");
            Integer categoryId = null;
            
            if (categoryParam != null && !categoryParam.isEmpty()) {
                try {
                    categoryId = Integer.parseInt(categoryParam);
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Invalid category ID: " + categoryParam, e);
                }
            }
            
            LOGGER.info("Filter params - Status: " + status + ", Category ID: " + categoryId + ", Search: " + search);
            
            // Lấy tham số phân trang
            int page = 1;
            try {
                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.isEmpty()) {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) page = 1;
                }
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid page number", e);
            }
            
            LOGGER.info("Page: " + page + ", Posts per page: " + POSTS_PER_PAGE);
            
            // Lấy tổng số bài viết
            int totalPosts = postDAO.countPosts(status, categoryId, search);
            int totalPages = (int) Math.ceil((double) totalPosts / POSTS_PER_PAGE);
            
            LOGGER.info("Total posts: " + totalPosts + ", Total pages: " + totalPages);
            
            // Lấy danh sách bài viết cho trang hiện tại
            List<Post> posts;
            try {
                if ("ADMIN".equals(currentUser.getRole())) {
                    // Admin xem tất cả bài viết
                    LOGGER.info("Getting posts for ADMIN");
                    posts = postDAO.findAllWithFilters(page, POSTS_PER_PAGE, status, categoryId, search);
                } else {
                    // Editor chỉ xem bài viết của mình
                    LOGGER.info("Getting posts for author ID: " + currentUser.getId().intValue());
                    posts = postDAO.findByAuthorWithFilters(currentUser.getId().intValue(), page, POSTS_PER_PAGE, status, categoryId, search);
                }
                
                LOGGER.info("Retrieved " + posts.size() + " posts");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error retrieving posts", e);
                throw e; // Rethrow để xem stack trace đầy đủ
            }
            
            // Lấy danh sách tất cả danh mục cho bộ lọc
            List<Category> allCategories = categoryDAO.findAll();
            
            // Đặt các thuộc tính cho JSP
            request.setAttribute("posts", posts);
            request.setAttribute("allCategories", allCategories);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalPosts", totalPosts);
            
            request.getRequestDispatcher("/admin/posts.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unhandled exception in doGet", e);
            throw e; // Rethrow để xem stack trace đầy đủ
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Kiểm tra quyền truy cập
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/sign-in");
            return;
        }
        
        String action = request.getParameter("action");
        LOGGER.info("AdminPostsServlet.doPost called with action: " + action);
        
        try {
            if ("add".equals(action)) {
                // Chuyển hướng đến trang thêm bài viết
                LOGGER.info("Redirecting to post form for adding new post");
                response.sendRedirect(request.getContextPath() + "/admin/post-form");
            } else if ("edit".equals(action)) {
                // Chuyển hướng đến trang sửa bài viết
                String postId = request.getParameter("id");
                LOGGER.info("Redirecting to post form for editing post with ID: " + postId);
                response.sendRedirect(request.getContextPath() + "/admin/post-form?id=" + postId);
            } else if ("delete".equals(action)) {
                deletePost(request, response);
            } else if ("change-status".equals(action)) {
                changePostStatus(request, response);
            } else {
                LOGGER.warning("Unknown action: " + action);
                response.sendRedirect(request.getContextPath() + "/admin/posts");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in doPost", e);
            request.getSession().setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/posts");
        }
    }

    private void deletePost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            
            // Kiểm tra quyền xóa bài viết
            User currentUser = (User) request.getSession().getAttribute("currentUser");
            Post post = postDAO.findById(id);
            
            if (post == null) {
                request.getSession().setAttribute("errorMessage", "Không tìm thấy bài viết!");
                response.sendRedirect(request.getContextPath() + "/admin/posts");
                return;
            }
            
            // Chỉ admin hoặc tác giả mới được xóa bài viết
            if (!"ADMIN".equals(currentUser.getRole()) && post.getAuthorId() != currentUser.getId().intValue()) {
                request.getSession().setAttribute("errorMessage", "Bạn không có quyền xóa bài viết này!");
                response.sendRedirect(request.getContextPath() + "/admin/posts");
                return;
            }
            
            boolean success = postDAO.delete(id);
            
            if (success) {
                LOGGER.info("Xóa bài viết thành công với ID: " + id);
                request.getSession().setAttribute("successMessage", "Xóa bài viết thành công!");
            } else {
                LOGGER.warning("Xóa bài viết thất bại với ID: " + id);
                request.getSession().setAttribute("errorMessage", "Xóa bài viết thất bại!");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa bài viết", e);
            request.getSession().setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            throw e; // Thêm dòng này để xem stack trace đầy đủ
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/posts");
    }

    private void changePostStatus(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String status = request.getParameter("status");
            
            // Kiểm tra quyền thay đổi trạng thái
            User currentUser = (User) request.getSession().getAttribute("currentUser");
            Post post = postDAO.findById(id);
            
            if (post == null) {
                request.getSession().setAttribute("errorMessage", "Không tìm thấy bài viết!");
                response.sendRedirect(request.getContextPath() + "/admin/posts");
                return;
            }
            
            // Chỉ admin hoặc tác giả mới được thay đổi trạng thái
            if (!"ADMIN".equals(currentUser.getRole()) && post.getAuthorId() != currentUser.getId().intValue()) {
                request.getSession().setAttribute("errorMessage", "Bạn không có quyền thay đổi trạng thái bài viết này!");
                response.sendRedirect(request.getContextPath() + "/admin/posts");
                return;
            }
            
            boolean success = postDAO.updateStatus(id, status);
            
            if (success) {
                LOGGER.info("Cập nhật trạng thái bài viết thành công: " + status);
                request.getSession().setAttribute("successMessage", "Cập nhật trạng thái bài viết thành công!");
            } else {
                LOGGER.warning("Cập nhật trạng thái bài viết thất bại!");
                request.getSession().setAttribute("errorMessage", "Cập nhật trạng thái bài viết thất bại!");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thay đổi trạng thái bài viết", e);
            request.getSession().setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            throw e; // Thêm dòng này để xem stack trace đầy đủ
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/posts");
    }
} 