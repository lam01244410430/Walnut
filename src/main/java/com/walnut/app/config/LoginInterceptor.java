package com.walnut.app.config; // 这里的包名要改成你项目的包名 (Sửa thành package của bạn)

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器 (Bộ chặn đăng nhập)
 * 作用：自动检查用户是否已登录 (Tác dụng: Tự động kiểm tra xem user đã login chưa)
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取 Session (Lấy Session)
        HttpSession session = request.getSession();

        // 2. 检查 "user" 是否存在 (Kiểm tra user có tồn tại không)
        Object user = session.getAttribute("user");

        if (user != null) {
            // 已登录，放行 (Đã đăng nhập -> Cho qua)
            return true;
        }

        // 3. 未登录，重定向到登录页 (Chưa đăng nhập -> Đá về trang Login)
        // System.out.println("用户未登录，被拦截！(User chưa login, bị chặn!)");
        response.sendRedirect("/login");
        return false;
    }
}