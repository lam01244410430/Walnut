package com.walnut.app.config; // 这里的包名要改成你项目的包名 (Sửa thành package của bạn)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 网站配置类 (Cấu hình Website)
 * 作用：配置哪些页面不需要登录就能访问 (Tác dụng: Cấu hình trang nào không cần login cũng xem được)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器 (Đăng ký bộ chặn)
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**") // 1. 默认拦截所有路径 (Mặc định chặn TẤT CẢ)
                .excludePathPatterns(   // 2. 排除以下路径 (TRỪ NHỮNG TRANG SAU LÀ ĐƯỢC VÀO):
                        "/",            // -> 首页 (Trang chủ gốc)
                        "/index",       // -> 首页 (Trang chủ index)
                        "/login",       // -> 登录页 (Trang login)
                        "/register",    // -> 注册页 (Trang đăng ký)
                        "/error",       // -> 错误页 (Trang lỗi)
                        "/css/**",      // -> 样式文件 (File CSS)
                        "/js/**",       // -> 脚本文件 (File JS)
                        "/images/**",   // -> 图片 (Ảnh)
                        "/fonts/**"     // -> 字体 (Font)
                );
    }
}