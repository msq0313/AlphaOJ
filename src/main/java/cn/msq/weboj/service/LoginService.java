package cn.msq.weboj.service;

import cn.msq.weboj.object.User;
import cn.msq.weboj.dao.Access;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LoginService {
    @Resource
    private Access login;

    public boolean userExists(String username) {
        User user = login.selectUserByName(username);
        return user != null;
    }

    public boolean login(String username, String password) {
        User user = login.selectUserByName(username);
        return user != null && password.equals(user.getPassword());
    }

    public void register(String username, String password) {
        login.insertUser(new User(username, password));
    }

    public User getUserByName(String username) {
        return login.selectUserByName(username);
    }
}
