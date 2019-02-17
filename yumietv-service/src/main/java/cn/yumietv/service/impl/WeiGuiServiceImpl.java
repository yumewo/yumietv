package cn.yumietv.service.impl;

import cn.yumie.service.WeiGuiService;
import cn.yumietv.entity.User;
import cn.yumietv.entity.UserIp;
import cn.yumietv.mapper.UserIpMapper;
import cn.yumietv.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: yumie
 * @Date: 2019/2/2 12:06
 * @Description:
 */
@Service
public class WeiGuiServiceImpl implements WeiGuiService {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserIpMapper userIpMapper;
    @Autowired
    UserMapper userMapper;

    public void weigui(String ip, User user) {
        UserIp userIp = userIpMapper.selectOne(new QueryWrapper<UserIp>().eq("ip", ip));
        if (userIp == null) {
            //违规一次,此ip必须登录才能观看视频
            userIp = new UserIp();
            userIp.setIp(ip);
            userIp.setDengji(1);
            userIpMapper.insert(userIp);
        } else if (userIp.getDengji() == 1) {
            //违规两次,此ip+用户封禁15天
            user.setStatus("关进小黑屋15天");
            userMapper.update(user, new UpdateWrapper<User>().eq("username", user.getUsername()));
            userIp.setDengji(2);
            userIpMapper.update(userIp, new QueryWrapper<UserIp>().eq("ip", ip));
            redisTemplate.opsForValue().set("xiaoheiwu:" + ip, "2", 15, TimeUnit.DAYS);
        } else {
            //违规三次,永封
            user.setStatus("账号已被永封");
            userIp.setDengji(3);
            userMapper.update(user, new UpdateWrapper<User>().eq("username", user.getUsername()));
            userIpMapper.update(userIp, new QueryWrapper<UserIp>().eq("ip", ip));
        }
    }
}
