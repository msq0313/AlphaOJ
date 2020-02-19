package cn.msq.weboj.object;

public class Path {
    private Integer id;
    private String questionPath;
    private String patternPath;

    public Path() {
    }

    public Path(Integer id, String questionPath, String patternPath) {
        this.id = id;
        this.questionPath = questionPath;
        this.patternPath = patternPath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestionPath() {
        return questionPath;
    }

    public void setQuestionPath(String questionPath) {
        this.questionPath = questionPath;
    }

    public String getPatternPath() {
        return patternPath;
    }

    public void setPatternPath(String patternPath) {
        this.patternPath = patternPath;
    }
}
