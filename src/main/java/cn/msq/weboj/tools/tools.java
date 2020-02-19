package cn.msq.weboj.tools;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

public class tools {
    //获取题目、模板、测试等字符串信息
    public static String beString(File file, String charSet) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName(charSet)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            if (content.toString().length() > 0) {
                content.deleteCharAt(content.length() - 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
