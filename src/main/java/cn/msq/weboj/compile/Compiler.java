package cn.msq.weboj.compile;

import javax.tools.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Compiler {
    //传入测试文件名、测试文件内容字符串，题解类名，题解内容
    //传入诊断信息收集器对象
    // 返回Map类型，<类名，字节码文件>
    public static Map<String, byte[]> compile(Map<String, String> sources, DiagnosticCollector<JavaFileObject> compileCollector) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager stdManager = compiler.getStandardFileManager(null, null, null);
        //创建java文件列表
        ArrayList<JavaFileObject> javaFileObjects = new ArrayList<>();
        for (String key : sources.keySet()) {
            String source = sources.get(key);
            //将用户输入的题解内容转换成java源文件
            JavaFileObject javaFileObject = JavaBuilder.makeStringSource(key, source);
            //列表中增加生成的java源文件
            javaFileObjects.add(javaFileObject);
        }
        try (JavaBuilder manager = new JavaBuilder(stdManager)) {
            //生成编译任务
            JavaCompiler.CompilationTask task = compiler.getTask(null, manager, compileCollector, null, null, javaFileObjects);
            if (task.call()) {
                return manager.getClassBytes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //存储类方法
    public static class MemoryClassLoader extends URLClassLoader {
        Map<String, byte[]> classBytes = new HashMap<>();

        public MemoryClassLoader(Map<String, byte[]> classBytes) {
            super(new URL[0], MemoryClassLoader.class.getClassLoader());
            this.classBytes.putAll(classBytes);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] buf = classBytes.get(name);
            if (buf == null) {
                return super.findClass(name);
            }
            classBytes.remove(name);
            return defineClass(name, buf, 0, buf.length);
        }
    }
}

