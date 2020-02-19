package cn.msq.weboj.exception;

public class TimeoutException extends Exception {
    public TimeoutException() {
        super("程序超时！");
    }
}
