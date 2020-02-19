package cn.msq.weboj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//启动注解
@SpringBootApplication
//MapperScan作用：指定要变成实现类的接口所在的包，然后包下面的所有接口在编译之后都会生成相应的实现类
@MapperScan("cn.msq.weboj.dao")
public class WebojApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebojApplication.class, args);
    }

}
