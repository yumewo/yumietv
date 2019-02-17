package cn.yumietv.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import cn.yumie.service.UserService;
import cn.yumietv.entity.UserIp;
import cn.yumietv.mapper.UserIpMapper;
import cn.yumietv.utils.FastJsonUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.yumietv.entity.User;
import cn.yumietv.mapper.UserMapper;
import cn.yumietv.utils.EmailUtils;
import cn.yumietv.utils.YumieResult;

/**
 * 用户管理Service
 *
 * @author yumie
 * @Date 2018年12月15日 下午10:50:18
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserIpMapper userIpMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${SESSION_PRE}")
    private String SESSION_PRE;
    @Value("${SESSION_EXPIRE}")
    private Integer SESSION_EXPIRE;

    public YumieResult usernameIsExist(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            return YumieResult.build(500, "那个混蛋抢占了先机");
        }
        return YumieResult.ok();
    }

    public YumieResult insertUser(User user, String ip) {
        //用户是否存在次数
        int count = 0;
        count = count + userMapper.selectCount(new QueryWrapper<User>().eq("username", user.getUsername()));
        count = count + userMapper.selectCount(new QueryWrapper<User>().eq("email", user.getEmail()));
        //用户存在,表单提交异常
        if (count > 0) {
            return YumieResult.build(500, "表单提交异常!");
        }
        //生成用户id
        String userid = UUID.randomUUID().toString().replace("-", "");
        user.setId(userid);

        UserIp userIp = new UserIp();
        userIp.setIp(ip);
        userIp.setIsRegister(1);
        userIpMapper.insert(userIp);

        //对密码就行MD5加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        userMapper.insert(user);
        //将用户信息保存到redis
        String token = saveToken(user);
        return YumieResult.ok(token);
    }

    public YumieResult emailIsExist(String email) {
        if (!EmailUtils.isEmail(email)) {
            return YumieResult.build(500, "格式不对啊喂");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            return YumieResult.build(500, "邮箱已被注册QAQ");
        }
        return YumieResult.ok();
    }

    public YumieResult loginByUsernameOrEmail(String str, String password, String ip) {
        if (StringUtils.isNotBlank(password) || StringUtils.isNotBlank(str)) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            if (EmailUtils.isEmail(str)) {
                queryWrapper.eq("email", str);
            } else {
                queryWrapper.eq("username", str);
            }
            User user = userMapper.selectOne(queryWrapper);
            if (user != null) {
                String message = "";
                if (StringUtils.isNotBlank(user.getStatus())) {
                    if (user.getStatus().contains("15")) {
                        Object o = redisTemplate.opsForValue().get("xiaoheiwu:" + ip);
                        if (o != null) {
                            return YumieResult.build(500, user.getStatus());
                        }
                    } else {
                        return YumieResult.build(500, user.getStatus());
                    }

                }
                String md5 = DigestUtils.md5DigestAsHex(password.getBytes()).toString();
                if (md5.equals(user.getPassword())) {
                    String token = saveToken(user);
                    return YumieResult.ok(token);
                }
            }
        }
        return YumieResult.build(500, "用户名或密码错误");
    }

    protected String saveToken(User user) {
        //清空密码保证安全
        user.setPassword(null);
        //生成id拼接
        String token = UUID.randomUUID().toString();
        token = SESSION_PRE + token;
        redisTemplate.opsForValue().set(token, FastJsonUtil.bean2Json(user), SESSION_EXPIRE, TimeUnit.SECONDS);
        return token;
    }

    public YumieResult updateUser(User user) {
        String email = user.getEmail();
        String password = user.getPassword();
        if (StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
            return YumieResult.build(500, "表单提交异常!");
        }
        user = userMapper.selectOne(new QueryWrapper<User>().eq("email", email));
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        userMapper.update(user, new UpdateWrapper<User>().eq("id", user.getId()));
        return YumieResult.ok();
    }

}
