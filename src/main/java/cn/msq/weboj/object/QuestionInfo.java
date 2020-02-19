package cn.msq.weboj.object;

//保存问题的描述信息
public class QuestionInfo {
    private Integer id;
    private String question;
    private String pattern;

    public QuestionInfo() {
    }

    public QuestionInfo(Integer id, String question, String pattern) {
        this.id = id;
        this.question = question;
        this.pattern = pattern;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }


}
