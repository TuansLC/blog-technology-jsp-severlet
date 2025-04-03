package com.ptit.blogtechnology.servlet.admin;

import com.ptit.blogtechnology.dao.CategoryDAO;
import com.ptit.blogtechnology.dao.PostDAO;
import com.ptit.blogtechnology.dao.TagDAO;
import com.ptit.blogtechnology.model.Category;
import com.ptit.blogtechnology.model.Post;
import com.ptit.blogtechnology.model.Tag;
import com.ptit.blogtechnology.model.User;
import com.ptit.blogtechnology.utils.SlugGenerator;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin/post-form")
public class AdminPostFormServlet extends HttpServlet {
    private PostDAO postDAO;
    private CategoryDAO categoryDAO;
    private TagDAO tagDAO;
    private static final Logger LOGGER = Logger.getLogger(AdminPostFormServlet.class.getName());

    @Override
    public void init() throws ServletException {
        postDAO = new PostDAO();
        categoryDAO = new CategoryDAO();
        tagDAO = new TagDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        LOGGER.info("AdminPostFormServlet.doGet called");
        
        // Kiểm tra quyền truy cập
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/sign-in");
            return;
        }
        
        // Lấy danh sách danh mục và thẻ
        List<Category> categories = categoryDAO.findAll();
        List<Tag> tags = tagDAO.findAll();
        
        request.setAttribute("categories", categories);
        request.setAttribute("tags", tags);
        
        // Kiểm tra xem là thêm mới hay sửa
        String postIdParam = request.getParameter("id");
        if (postIdParam != null && !postIdParam.isEmpty()) {
            try {
                int postId = Integer.parseInt(postIdParam);
                Post post = postDAO.findById(postId);
                
                if (post != null) {
                    // Kiểm tra quyền sửa bài viết
                    if (!"ADMIN".equals(currentUser.getRole()) && post.getAuthorId() != currentUser.getId().intValue()) {
                        request.getSession().setAttribute("errorMessage", "Bạn không có quyền sửa bài viết này!");
                        response.sendRedirect(request.getContextPath() + "/admin/posts");
                        return;
                    }
                    
                    request.setAttribute("post", post);
                    request.setAttribute("mode", "edit");
                    
                    // Lấy danh mục và thẻ của bài viết
                    List<Category> postCategories = categoryDAO.findByPostId(postId);
                    List<Tag> postTags = tagDAO.findByPostId(postId);
                    
                    request.setAttribute("postCategories", postCategories);
                    request.setAttribute("postTags", postTags);
                } else {
                    request.getSession().setAttribute("errorMessage", "Không tìm thấy bài viết!");
                    response.sendRedirect(request.getContextPath() + "/admin/posts");
                    return;
                }
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid post ID: " + postIdParam, e);
                request.getSession().setAttribute("errorMessage", "ID bài viết không hợp lệ!");
                response.sendRedirect(request.getContextPath() + "/admin/posts");
                return;
            }
        } else {
            request.setAttribute("mode", "add");
        }
        
        request.getRequestDispatcher("/admin/post-form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        LOGGER.info("AdminPostFormServlet.doPost called");
        
        // Kiểm tra quyền truy cập
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/sign-in");
            return;
        }
        
        try {
            // Lấy dữ liệu từ form
            String mode = request.getParameter("mode");
            String title = request.getParameter("title");
            String slug = request.getParameter("slug");
            String content = request.getParameter("content");
            String excerpt = request.getParameter("excerpt");
            String featuredImage = request.getParameter("featuredImage");
            String status = request.getParameter("status");
            String[] categoryIds = request.getParameterValues("categories");
            String[] tagIds = request.getParameterValues("tags");
            String isFeaturedStr = request.getParameter("isFeatured");
            boolean isFeatured = "on".equals(isFeaturedStr);
            
            // Thêm log để debug
            LOGGER.info("Form data - Title: " + title + ", Slug: " + slug + ", Status: " + status);
            LOGGER.info("Categories: " + (categoryIds != null ? categoryIds.length : 0) + ", Tags: " + (tagIds != null ? tagIds.length : 0));
            
            LOGGER.info("Processing post - Title: " + title + ", Status: " + status);
            
            // Tạo slug tự động nếu không có
            if (slug == null || slug.trim().isEmpty()) {
                slug = SlugGenerator.toSlug(title);
                LOGGER.info("Generated slug: " + slug);
            }
            
            // Chuyển đổi danh sách ID danh mục và thẻ
            List<Integer> categoryIdList = new ArrayList<>();
            if (categoryIds != null) {
                for (String categoryId : categoryIds) {
                    categoryIdList.add(Integer.parseInt(categoryId));
                }
            }
            
            List<Integer> tagIdList = new ArrayList<>();
            if (tagIds != null) {
                for (String tagId : tagIds) {
                    tagIdList.add(Integer.parseInt(tagId));
                }
            }
            
            // Xử lý theo mode
            if ("add".equals(mode)) {
                // Thêm mới bài viết
                Post post = new Post();
                post.setTitle(title);
                post.setSlug(slug);
                post.setContent(content);
                post.setSummary(excerpt);
                post.setFeaturedImage(featuredImage);
                post.setStatus(status);
                post.setAuthorId(currentUser.getId().intValue());
                post.setFeatured(isFeatured);
                post.setCreatedAt(LocalDateTime.now());
                post.setUpdatedAt(LocalDateTime.now());
                
                if ("PUBLISHED".equals(status)) {
                    post.setPublishedAt(LocalDateTime.now());
                }
                
                // Lưu bài viết
                boolean success = postDAO.save(post);
                
                // Nếu lưu thành công, lưu thêm danh mục và thẻ
                if (success) {
                    // Lưu danh mục
                    for (Integer categoryId : categoryIdList) {
                        postDAO.savePostCategory(post.getId(), categoryId);
                    }
                    
                    // Lưu thẻ
                    for (Integer tagId : tagIdList) {
                        postDAO.savePostTag(post.getId(), tagId);
                    }
                    
                    LOGGER.info("Thêm bài viết thành công: " + title);
                    request.getSession().setAttribute("successMessage", "Thêm bài viết thành công!");
                } else {
                    LOGGER.warning("Thêm bài viết thất bại: " + title);
                    request.getSession().setAttribute("errorMessage", "Thêm bài viết thất bại!");
                }
            } else if ("edit".equals(mode)) {
                // Cập nhật bài viết
                int postId = Integer.parseInt(request.getParameter("id"));
                Post post = postDAO.findById(postId);
                
                if (post != null) {
                    // Kiểm tra quyền sửa bài viết
                    if (!"ADMIN".equals(currentUser.getRole()) && post.getAuthorId() != currentUser.getId().intValue()) {
                        request.getSession().setAttribute("errorMessage", "Bạn không có quyền sửa bài viết này!");
                        response.sendRedirect(request.getContextPath() + "/admin/posts");
                        return;
                    }
                    
                    post.setTitle(title);
                    post.setSlug(slug);
                    post.setContent(content);
                    post.setSummary(excerpt);
                    post.setFeaturedImage(featuredImage);
                    post.setFeatured(isFeatured);
                    post.setUpdatedAt(LocalDateTime.now());
                    
                    // Cập nhật trạng thái và thời gian xuất bản
                    if ("PUBLISHED".equals(status) && !"PUBLISHED".equals(post.getStatus())) {
                        post.setPublishedAt(LocalDateTime.now());
                    }
                    post.setStatus(status);
                    
                    // Thêm log trước khi gọi update
                    LOGGER.info("Updating post - ID: " + post.getId() + ", Title: " + post.getTitle() + ", Categories: " + categoryIdList.size() + ", Tags: " + tagIdList.size());

                    // Gọi phương thức update
                    boolean success = postDAO.update(post, categoryIdList, tagIdList);

                    // Log kết quả
                    LOGGER.info("Update result: " + success);
                    
                    if (success) {
                        LOGGER.info("Cập nhật bài viết thành công: " + title);
                        request.getSession().setAttribute("successMessage", "Cập nhật bài viết thành công!");
                    } else {
                        LOGGER.warning("Cập nhật bài viết thất bại: " + title);
                        request.getSession().setAttribute("errorMessage", "Cập nhật bài viết thất bại!");
                    }
                } else {
                    LOGGER.warning("Không tìm thấy bài viết với ID: " + postId);
                    request.getSession().setAttribute("errorMessage", "Không tìm thấy bài viết!");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xử lý bài viết", e);
            request.getSession().setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            throw e; // Thêm dòng này để xem stack trace đầy đủ
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/posts");
    }
} 