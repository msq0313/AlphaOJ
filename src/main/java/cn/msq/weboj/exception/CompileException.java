package cn.msq.weboj.exception;

public class CompileException extends Exception {
    public CompileException(String msg) {
        super("编译错误\n" + msg);
    }
}
