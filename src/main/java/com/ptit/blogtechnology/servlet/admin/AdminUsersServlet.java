package com.ptit.blogtechnology.servlet.admin;

import com.ptit.blogtechnology.dao.UserDAO;
import com.ptit.blogtechnology.model.User;
import com.ptit.blogtechnology.utils.PasswordHasher;
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

@WebServlet("/admin/users")
public class AdminUsersServlet extends HttpServlet {
    private UserDAO userDAO;
    private static final Logger LOGGER = Logger.getLogger(AdminUsersServlet.class.getName());

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        LOGGER.info("AdminUsersServlet.doGet called");
        
        // Kiểm tra quyền truy cập
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/sign-in");
            return;
        }
        
        List<User> users = userDAO.findAll();
        request.setAttribute("users", users);
        request.setAttribute("currentUser", currentUser);
        
        request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Kiểm tra quyền truy cập
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/sign-in");
            return;
        }
        
        String action = request.getParameter("action");
        LOGGER.info("AdminUsersServlet.doPost called with action: " + action);
        
        try {
            if ("add".equals(action)) {
                addUser(request, response);
            } else if ("edit".equals(action)) {
                editUser(request, response);
            } else if ("delete".equals(action)) {
                deleteUser(request, response);
            } else {
                LOGGER.warning("Unknown action: " + action);
                response.sendRedirect(request.getContextPath() + "/admin/users");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in doPost", e);
            request.getSession().setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }
    
    private void addUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String username = request.getParameter("username");
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String role = request.getParameter("role");
            
            LOGGER.info("Adding user - Username: " + username + ", Email: " + email + ", Role: " + role);
            
            // Kiểm tra username đã tồn tại chưa
            User existingUser = userDAO.findByUsername(username);
            if (existingUser != null) {
                request.getSession().setAttribute("errorMessage", "Tên đăng nhập đã tồn tại!");
                response.sendRedirect(request.getContextPath() + "/admin/users");
                return;
            }
            
            // Kiểm tra email đã tồn tại chưa
            existingUser = userDAO.findByEmail(email);
            if (existingUser != null) {
                request.getSession().setAttribute("errorMessage", "Email đã tồn tại!");
                response.sendRedirect(request.getContextPath() + "/admin/users");
                return;
            }
            
            // Mã hóa mật khẩu
            String passwordHash = PasswordHasher.hashPassword(password);
            
            User user = new User();
            user.setUsername(username);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPasswordHash(passwordHash);
            user.setRole(role);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            
            boolean success = userDAO.save(user);
            
            if (success) {
                LOGGER.info("Thêm người dùng thành công: " + username);
                request.getSession().setAttribute("successMessage", "Thêm người dùng thành công!");
            } else {
                LOGGER.warning("Thêm người dùng thất bại: " + username);
                request.getSession().setAttribute("errorMessage", "Thêm người dùng thất bại!");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in addUser", e);
            throw e; // Rethrow để có thể xem stack trace đầy đủ
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
    
    private void editUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            String userName = request.getParameter("username");
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String role = request.getParameter("role");
            
            LOGGER.info("Editing user - ID: " + id + ", Username: " + userName + ", FullName: " + fullName + ", Email: " + email + ", Role: " + role);
            
            User user = userDAO.findById(id);
            if (user != null) {
                // Kiểm tra username có bị thay đổi không
                if (!user.getUsername().equals(userName)) {
                    // Kiểm tra username mới đã tồn tại chưa
                    User existingUser = userDAO.findByUsername(userName);
                    if (existingUser != null) {
                        request.getSession().setAttribute("errorMessage", "Tên đăng nhập đã tồn tại!");
                        response.sendRedirect(request.getContextPath() + "/admin/users");
                        return;
                    }
                }
                
                // Kiểm tra email đã tồn tại chưa (nếu thay đổi)
                if (!user.getEmail().equals(email)) {
                    User existingUser = userDAO.findByEmail(email);
                    if (existingUser != null) {
                        request.getSession().setAttribute("errorMessage", "Email đã tồn tại!");
                        response.sendRedirect(request.getContextPath() + "/admin/users");
                        return;
                    }
                }

                user.setUsername(userName);
                user.setFullName(fullName);
                user.setEmail(email);
                
                // Cập nhật mật khẩu nếu có
                if (password != null && !password.trim().isEmpty()) {
                    String passwordHash = PasswordHasher.hashPassword(password);
                    user.setPasswordHash(passwordHash);
                }
                
                user.setRole(role);
                user.setUpdatedAt(LocalDateTime.now());
                
                boolean success = userDAO.update(user);
                
                if (success) {
                    LOGGER.info("Cập nhật người dùng thành công: " + user.getUsername());
                    request.getSession().setAttribute("successMessage", "Cập nhật người dùng thành công!");
                } else {
                    LOGGER.warning("Cập nhật người dùng thất bại: " + user.getUsername());
                    request.getSession().setAttribute("errorMessage", "Cập nhật người dùng thất bại!");
                }
            } else {
                LOGGER.warning("Không tìm thấy người dùng với ID: " + id);
                request.getSession().setAttribute("errorMessage", "Không tìm thấy người dùng!");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in editUser", e);
            throw e; // Thêm dòng này để xem stack trace đầy đủ
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
    
    private void deleteUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            
            LOGGER.info("Deleting user with ID: " + id);
            
            // Không cho phép xóa tài khoản đang đăng nhập
            User currentUser = (User) request.getSession().getAttribute("currentUser");
            if (currentUser.getId().equals(id)) {
                LOGGER.warning("Không thể xóa tài khoản đang đăng nhập: " + id);
                request.getSession().setAttribute("errorMessage", "Không thể xóa tài khoản đang đăng nhập!");
                response.sendRedirect(request.getContextPath() + "/admin/users");
                return;
            }
            
            boolean success = userDAO.delete(id);
            
            if (success) {
                LOGGER.info("Xóa người dùng thành công với ID: " + id);
                request.getSession().setAttribute("successMessage", "Xóa người dùng thành công!");
            } else {
                LOGGER.warning("Xóa người dùng thất bại với ID: " + id);
                request.getSession().setAttribute("errorMessage", "Xóa người dùng thất bại!");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in deleteUser", e);
            throw e; // Thêm dòng này để xem stack trace đầy đủ
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
} 