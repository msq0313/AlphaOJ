## AlphaOJ在线判题系统实现介绍

#### 一、项目成员及分工

#### 二、项目架构介绍

1. ##### 项目介绍

   - AlphaOJ是一个在线判题系统， 用户可以在线提交java源代码，系统对源代码进行编译和执行，并通过预先设计的测试用例来检验程序源代码的正误。 

2. ##### 架构介绍

   - 采用springboot框架，逻辑清晰，能够快速搭建web程序，且在市场中广泛应用。

   - 架构形式：

     - 主流浏览器——springMVC——spring框架——MyBatis

     - 展示页面——    表现层——        业务层——       持久层

   - 其中springMVC：

     - M：model模型  JavaBean
     - V： view视图  html + themleaf + bootstrap框架
     - C： controller控制器  Servlet

   - 数据库选用：MySQL数据库

#### 三、核心业务的实现方式

1. ##### 判题过程

   - 规定用户传入的代码的类与类名，接收用户在输入框中输入的内容。变量类型及名称已在模板中给出，用户不用再进行数据的读入操作，只需专注于题目解答即可。
   - 将用户传入的代码内容保存为.java文件。
   - 由于Java源文件相对较容易生成，是纯文本文件。而Java的编译器是自举的，也就是说Java编译器就是用Java本身编写的，因此从JDK 1.6开始，编译器相关API就一直在SDK中，所以，我们也不需要通过调用命令行的方式来控制javac来编译我们动态生成的Java源文件。
   - 调用JavaCompiler（java自身API），编译.java文件（包括题目对应的单元测试文件及用户文件），生成字节码文件。
   - 如果未生成字节码文件，则表示编译错误，抛出编译错误。
   - 创建任务：调用动态类加载器加载生成的字节码文件。开始计时，利用JUnitCore.runClasses运行单元测试文件，即可使程序运行，最后结束计时。
   - 向线程池提交任务，任务开始执行，收集超时错误，运行时错误，结果错误（执行错误）并返回信息。

2. ##### 其他业务

   - 注册登录
     - 注册时判断用户名是否存在，用户名密码是否合法，返回信息。
     - 登录时判断用户名密码是否正确，返回信息。
   - 查看历史记录
     - 历史记录保存在数据库中，用户可查看当前账号的历史记录

#### 四、项目代码介绍

1. ##### 代码结构

src/main下
- java
  - compile  编译运行部分
  - controller  接收前端请求，前端页面逻辑，调用服务
  - dao  数据库接口文件（ Data Access Object ）
  - exception  自定义异常类
  - object 对象类
  - service  服务层
  - tools  提取文件信息
  - WebojApplication  程序运行入口
- resources
  - mybatis  持久层，存有sql命令，与数据库交互
  - templates  前端页面
  - application.yaml  springboot配置文件

2. **后端部分代码展示**

~~~java
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

~~~



3. **数据库部分代码展示**

由Mybatis操作数据库

~~~xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="cn.msq.weboj.dao.Access">
    <insert id="insertUser" parameterType="cn.msq.weboj.object.User">
        INSERT INTO user(username, password)
        VALUES (#{username},#{password})
    </insert>

    <select id="selectUserByName" resultType="cn.msq.weboj.object.User" parameterType="java.lang.String">
        SELECT uId,username,password
        FROM user
        WHERE username=#{0}
    </select>

    <select id="getAllQuestionInfoPath" resultType="cn.msq.weboj.object.Path">
        SELECT id,questionPath,patternPath
        FROM path
        ORDER BY id
    </select>
    <select id="getQuestionInfoPathById" parameterType="java.lang.Integer"
            resultType="cn.msq.weboj.object.Path">
        SELECT id,questionPath,patternPath
        FROM path
        WHERE id=#{0}
    </select>
    <select id="selectAnswerByUId" resultType="cn.msq.weboj.object.Answer">
        SELECT commitTime,isRight,questionPath,msg
        FROM record,path
        WHERE record.qId=path.id AND
              uId=#{0}
        ORDER BY commitTime DESC
    </select>
    <select id="getTestCasePathById" parameterType="java.lang.Integer"
            resultType="java.lang.String">
        SELECT testPath
        FROM path
        WHERE id=#{0}
    </select>
    <insert id="insertAnswer" parameterType="cn.msq.weboj.object.Answer">
        INSERT INTO record(commitTime,isRight,msg,qId,uId)
        VALUES (#{commitTime},#{isRight},#{msg},#{qId},#{uId})
    </insert>
</mapper>
~~~

4. **前端部分代码展示**

~~~html
<!DOCTYPE html>
<html lang="en" charset="UTF-8" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>AlphaOJ主页</title>
    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <!-- 最新版本的 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css">
    <!-- 可选的 Bootstrap 主题文件（一般不用引入） -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap-theme.min.css">
</head>

<body>
    <nav class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">

            <div class="navbar-header">
                <button class="navbar-toggle" data-toggle="collapse" data-target="#res-nav">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a href="/" class="navbar-brand"><strong>AlphaOJ</strong></a>
            </div>

            <div class="collapse navbar-collapse" id="res-nav">
                <form action="" class="navbar-form navbar-left">
                    <label>
                        <input type="text" placeholder="输入搜索内容" class="form-control">
                    </label>
                    <button type="submit">
                        <span class="glyphicon glyphicon-search"></span>
                    </button>
                </form>
    <!--            <div th:include="header::#head2"></div>-->
                <div class="navbar-right">
                    <ul class="nav navbar-nav">
                        <li><a th:href="@{/login}">登录</a></li>
                        <li><a th:href="@{/register}">注册</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </nav>
    <dev class="container">
        <p><br><br><br><br><br><br><br><br><br><br></p>
        <div class="container" align="center">
            <div class="starter">
                <h1><strong>AlphaOJ</strong></h1>
                <p class="lead">Just do it!</p>
            </div>
        </div>
    </dev>

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/js/bootstrap.min.js"></script>

</body>

</html>
~~~

