package cn.msq.weboj.controller;

import cn.msq.weboj.object.Answer;
import cn.msq.weboj.object.QuestionInfo;
import cn.msq.weboj.object.User;
import cn.msq.weboj.exception.RuntimeException;
import cn.msq.weboj.exception.CompileException;
import cn.msq.weboj.exception.TimeoutException;
import cn.msq.weboj.service.LoginService;
import cn.msq.weboj.service.JudgeService;
import cn.msq.weboj.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class JudgeController {
    @Resource
    QuestionService questionService;
    @Resource
    LoginService loginService;
    @Resource
    JudgeService judgeService;

    //ModelAndView构造方法可以指定返回的页面名称，也可以通过setViewName()方法跳转到指定的页面
    @GetMapping("oj/{id}")
    public ModelAndView oj(@PathVariable("id") Integer id) {
        QuestionInfo questionInfo = questionService.findQuestionById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModel().put("questionInfo", questionInfo);
        modelAndView.setViewName("judge");
        return modelAndView;
    }

    @PostMapping("judge")
    public ModelAndView judge(@ModelAttribute("id") int id, @ModelAttribute("content") String content, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Map<String, Object> model = modelAndView.getModel();
        String resultMsg;
        try {
            //根据session获取当前用户ID
            HttpSession session = request.getSession();
            String username = (String) session.getAttribute("user");
            User user = loginService.getUserByName(username);
            Integer uId = user.getUid();
            //运行判题，得到结果信息
            resultMsg = judgeService.judge(id, content, uId);
            if (resultMsg != null) {
                model.put("answer", true);
                model.put("msg", resultMsg);
            }
        } catch (CompileException | RuntimeException | TimeoutException e) {
            model.put("answer", false);
            model.put("msg", e.getMessage());
        }
        model.put("id", id);
        modelAndView.setViewName("result");
        return modelAndView;

    }

    @GetMapping("record")
    public ModelAndView answerHistory(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("user");
        User user = loginService.getUserByName(username);
        Integer uId = user.getUid();
        List<Answer> answers = questionService.findAllAnswerByUId(uId);

        ModelAndView modelAndView = new ModelAndView();
        Map<String, Object> model = modelAndView.getModel();
        model.put("answers", answers);
        modelAndView.setViewName("record");
        return modelAndView;
    }
}
