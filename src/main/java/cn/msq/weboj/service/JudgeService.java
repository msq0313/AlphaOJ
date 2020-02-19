package cn.msq.weboj.service;

import cn.msq.weboj.dao.Access;
import cn.msq.weboj.object.Answer;
import cn.msq.weboj.exception.RuntimeException;
import cn.msq.weboj.exception.CompileException;
import cn.msq.weboj.exception.TimeoutException;
import cn.msq.weboj.compile.Compiler;
import cn.msq.weboj.tools.tools;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class JudgeService {
    @Resource
    private Access question;

    //判题方法，抛出编译错误，超时错误，运行时错误
    public String judge(int id, String soluteString, Integer uId) throws TimeoutException, CompileException, RuntimeException {
        //通过测试文件路径生成文件对象
        File testFile = new File(question.getTestCasePathById(id));
        //创建映射集
        HashMap<String, String> sources = new HashMap<>();
        //获取测试文件名
        String testCaseFileName = testFile.getName();
        //映射测试文件名，测试文件转为字符串后的文件内容
        sources.put(testCaseFileName, tools.beString(testFile, "UTF-8"));
        //映射题解名，题解
        sources.put("Solution.java", soluteString);


//        JavaCompiler - 表示java编译器, run方法执行编译操作. 还有一种编译方式是先生成编译任务(CompilationTask), 让后调用CompilationTask的call方法执行编译任务
//        JavaFileObject - 表示一个java源文件对象
//        JavaFileManager - Java源文件管理类, 管理一系列JavaFileObject
//        Diagnostic - 表示一个诊断信息
//        DiagnosticListener - 诊断信息监听器, 编译过程触发. 生成编译task(JavaCompiler#getTask())或获取FileManager(JavaCompiler#getStandardFileManager())时需要传递DiagnosticListener以便收集诊断信息

        //java动态编译

        //DiagnosticCollector实现了诊断侦听器接口，用于将诊断信息收集在一个列表中
        DiagnosticCollector<JavaFileObject> compileCollector = new DiagnosticCollector<>();
        //编译生成字节码文件  public static Map<String, byte[]> compile(String javaName, String javaSrc)
        //单元测试用户上传的代码
        Map<String, byte[]> code = Compiler.compile(sources, compileCollector);
        //编译错误处理
        //未生成字节码文件
        if (code == null) {
            // 获取编译错误信息列表
            List<Diagnostic<? extends JavaFileObject>> compileErrorList = compileCollector.getDiagnostics();
            StringBuilder compileError = new StringBuilder();
            for (Diagnostic diagnostic : compileErrorList) {
                compileError.append("在第 ");
                //获取行数
                compileError.append(diagnostic.getLineNumber());
                compileError.append(" 行出错 ");
                //换行
                compileError.append(System.lineSeparator());
                //获取当前运行环境信息
                compileError.append(diagnostic.getMessage(Locale.getDefault()));
            }
            String errMsg = compileError.toString();
            record(0, errMsg, id, uId);
            //抛出编译异常，传递信息
            throw new CompileException(errMsg);
        }

        //动态类加载器
        Compiler.MemoryClassLoader classLoader = new Compiler.MemoryClassLoader(code);
        //反射调用Test.test(String className)方法
        //创建线程
        ExecutorService pool = Executors.newSingleThreadExecutor();
        //Callable接口位于java.util.concurrent包，这是一个泛型接口，里面只声明了一个call()方法，即运行call
        //建立任务
        Callable<String> runTask = new Callable<String>() {
            @Override
            public String call() throws Exception {
                //开始计时
                long startTime = System.currentTimeMillis();
                //结果对象
                Result result = null;
                try {
                    //测试文件结尾为.java，长度减5，即为类名
                    String testCaseClassName = testCaseFileName.substring(0, testCaseFileName.length() - 5);
                    //加载类
                    Class testCaseClass = classLoader.loadClass(testCaseClassName);
                    //测试并返回结果
                    result = JUnitCore.runClasses(testCaseClass);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                //结束计时
                long endTime = System.currentTimeMillis();

                //成功运行则返回运行用时
                assert result != null;
                if (result.wasSuccessful()) {
                    return ("用时: " + (endTime - startTime) + "ms");
                } else {
                    StringBuilder msg = new StringBuilder();
                    for (Failure failure : result.getFailures()) {
                        msg.append(failure.getException().getMessage());
                    }
                    //ExecutionException封装了正在执行的线程抛出的任何异常
                    throw new ExecutionException(new RuntimeException(msg.toString()));
                }
            }
        };
        //向线程池提交任务
        Future<String> res = pool.submit(runTask);
//        Future主要方法
//        cancel()：取消任务
//        get()：等待任务执行完成，并获取执行结果
//        get(long timeout, TimeUnit unit)：在指定的时间内会等待任务执行，超时则抛异常
        //获得运行结果
        String runResult;


        //检测其他非编译错误
        try {
            //设置5秒运行时间限制
            runResult = res.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            String errMsg = "程序终止！";
            record(0, errMsg, id, uId);
            //运行时错误
            throw new RuntimeException(errMsg);
        } catch (ExecutionException e) {
            String errMsg = "结果错误！" + e.getCause().getMessage();
            record(0, errMsg, id, uId);
            //运行时错误
            throw new RuntimeException(errMsg);
        } catch (java.util.concurrent.TimeoutException e) {
            String errMsg = "程序超时！";
            record(0, errMsg, id, uId);
            //超时错误
            throw new TimeoutException();
        } finally {
            pool.shutdown();
        }
        record(1, runResult, id, uId);
        return runResult;
    }

    //保存答题记录
    private void record(int isTrue, String msg, Integer qId, Integer uId) {
        //获取系统时间，保存答题时间
        Timestamp time = new Timestamp(System.currentTimeMillis());
        Answer answer = new Answer(time, isTrue, msg, qId, uId);
        question.insertAnswer(answer);
    }

}
