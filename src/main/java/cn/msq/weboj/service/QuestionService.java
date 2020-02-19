package cn.msq.weboj.service;

import cn.msq.weboj.dao.Access;
import cn.msq.weboj.object.Answer;
import cn.msq.weboj.tools.tools;
import cn.msq.weboj.object.QuestionInfo;
import cn.msq.weboj.object.Path;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Resource
    private Access question;

    //获取全部题目信息
    public List<QuestionInfo> findAllQuestions() {
        //创建题目路径
        List<Path> questionInfoPaths = question.getAllQuestionInfoPath();
        List<QuestionInfo> questionInfos = new ArrayList<>();
        for (Path questionInfoPath : questionInfoPaths) {
            QuestionInfo info = new QuestionInfo();
            //设置题目id
            info.setId(questionInfoPath.getId());
            //设置题目
            info.setQuestion(tools.beString(new File(questionInfoPath.getQuestionPath()), "UTF-8"));
            //设置模板
            info.setPattern(tools.beString(new File(questionInfoPath.getPatternPath()), "UTF-8"));
            questionInfos.add(info);
        }
        return questionInfos;
    }

    //根据题目编号获取题目信息
    public QuestionInfo findQuestionById(int id) {
        Path questionInfoPath = question.getQuestionInfoPathById(id);
        QuestionInfo info = new QuestionInfo();
        info.setId(questionInfoPath.getId());
        info.setQuestion(tools.beString(new File(questionInfoPath.getQuestionPath()), "UTF-8"));
        info.setPattern(tools.beString(new File(questionInfoPath.getPatternPath()), "UTF-8"));
        return info;
    }

    //获取问题提交结果列表
    public List<Answer> findAllAnswerByUId(Integer id) {
        List<Answer> answers = question.selectAnswerByUId(id);
        //遍历，依据文件路径读出文件内容
        for (Answer answer : answers) {
            File questionFile = new File(answer.getQuestionPath());
            answer.setQuestion(tools.beString(questionFile, "UTF-8"));
            answer.setMsg(answer.getMsg());
        }
        return answers;
    }
}
