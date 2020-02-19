package cn.msq.weboj.dao;

import cn.msq.weboj.object.Answer;
import cn.msq.weboj.object.Path;
import cn.msq.weboj.object.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface Access {
    //创建用户
    void insertUser(User user);
    //查找用户
    User selectUserByName(String username);
    //获取测试路径
    String getTestCasePathById(int id);
    //获取题目路径
    List<Path> getAllQuestionInfoPath();

    Path getQuestionInfoPathById(int id);

    void insertAnswer(Answer answer);

    List<Answer> selectAnswerByUId(Integer uId);
}
