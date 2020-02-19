package cn.msq.weboj.object;

import java.sql.Timestamp;

public class Answer {
    private Timestamp commitTime;
    private int isRight;
    private String questionPath;
    private String msg;
    private Integer qId;
    private Integer uId;
    private String question;

    public Answer() {
    }

    public Answer(Timestamp commitTime, int isRight, String questionPath, String msg) {
        this.commitTime = commitTime;
        this.isRight = isRight;
        this.questionPath = questionPath;
        this.msg = msg;
    }

    public Answer(Timestamp commitTime, int isRight, String msg, Integer qId, Integer uId) {
        this.commitTime = commitTime;
        this.isRight = isRight;
        this.msg = msg;
        this.qId = qId;
        this.uId = uId;
    }

    public Timestamp getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(Timestamp commitTime) {
        this.commitTime = commitTime;
    }

    public int getIsRight() {
        return isRight;
    }

    public void setIsRight(int isRight) {
        this.isRight = isRight;
    }

    public String getQuestionPath() {
        return questionPath;
    }

    public void setQuestionPath(String questionPath) {
        this.questionPath = questionPath;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getqId() {
        return qId;
    }

    public void setqId(Integer qId) {
        this.qId = qId;
    }

    public Integer getuId() {
        return uId;
    }

    public void setuId(Integer uId) {
        this.uId = uId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
