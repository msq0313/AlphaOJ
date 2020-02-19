package cn.msq.weboj.exception;

public class RuntimeException extends Exception {
    public RuntimeException(String msg) {
        super("运行异常\n" + msg);
    }
}
