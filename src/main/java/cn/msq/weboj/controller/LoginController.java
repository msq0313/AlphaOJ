package cn.msq.weboj.controller;

import cn.msq.weboj.object.QuestionInfo;
import cn.msq.weboj.service.LoginService;
import cn.msq.weboj.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {
    @Resource
    private LoginService loginService;
    @Resource
    private QuestionService questionService;

    //AlphaOJ主页，打开设置的端口即可见
    @GetMapping("/")
    public String index() {

        return "index";
    }

    //题目列表界面
    @GetMapping("problemList")
    public ModelAndView problemList() {
        List<QuestionInfo> questionInfos = questionService.findAllQuestions();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModel().put("questionInfos", questionInfos);
        modelAndView.setViewName("problemList");
        return modelAndView;
    }

    //登录
    @GetMapping("login")
    public String login(HttpServletRequest request) {
        return "login";
    }

    //注册
    @GetMapping("register")
    public String register() {
        return "register";
    }

    //返回首页
    @GetMapping("quit")
    public String quit() {
        return "index";
    }

    //登录信息检测
    @PostMapping("login")
    public ModelAndView loginAssert(HttpServletRequest request, @ModelAttribute("username") String username, @ModelAttribute("password") String password) {
        ModelAndView modelAndView = new ModelAndView();
        Map<String, Object> model = modelAndView.getModel();
        boolean flag = loginService.login(username, password);

        if (!flag) {
            model.put("userError", "用户名或密码错误");
        }

        if (flag) {
            //保存用户名session，便于后面使用及页面上用户信息展示
            HttpSession session = request.getSession();
            session.setAttribute("user", username);
            //重定向至页面题目列表
            modelAndView.setViewName("redirect:problemList");

        } else {
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }

    //注册信息检测
    @PostMapping("register")
    public ModelAndView registerAssert(@ModelAttribute("username") String username, @ModelAttribute("password") String password) {
        ModelAndView modelAndView = new ModelAndView();
        Map<String, Object> model = modelAndView.getModel();
        int nameLength = username.length();
        boolean flag = true;
        //检测用户名是否已经存在
        if (loginService.userExists(username)) {
            model.put("userError", "用户名被占用");
            flag = false;
        }
        //检测用户名长度
        if (nameLength > 10 || nameLength < 6) {
            model.put("userError", "用户名长度：6-10");
            flag = false;
        }
        //检测密码长度
        int passwordLength = password.length();
        if (passwordLength > 10 || passwordLength < 6) {
            model.put("passwordError", "密码长度：6-10");
            flag = false;
        }
        //如果信息错误，将停留在注册界面
        if (!flag) {
            modelAndView.setViewName("register");
        } else {
            loginService.register(username, password);
            modelAndView.setViewName("login");
            model.put("Msg", "成功注册");
        }
        return modelAndView;
    }

}
