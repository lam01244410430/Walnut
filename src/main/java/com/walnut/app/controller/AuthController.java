package com.walnut.app.controller;

import com.walnut.app.model.Admin;
import com.walnut.app.model.Expert;
import com.walnut.app.model.Farmer;
import com.walnut.app.repository.AdminRepository;
import com.walnut.app.repository.ExpertRepository;
import com.walnut.app.repository.FarmerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(HttpSession session) {
        session.invalidate(); // Xóa session
        return "redirect:/login"; // Chuyển hướng về trang đăng nhập
    }
}