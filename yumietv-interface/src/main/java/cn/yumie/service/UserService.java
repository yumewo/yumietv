package cn.yumie.service;

import cn.yumietv.entity.User;
import cn.yumietv.utils.YumieResult;

public interface UserService {
    YumieResult usernameIsExist(String username);
    YumieResult insertUser(User user, String ip);
    YumieResult emailIsExist(String email);
    YumieResult loginByUsernameOrEmail(String str, String password, String ip);
    YumieResult updateUser(User user);
}
