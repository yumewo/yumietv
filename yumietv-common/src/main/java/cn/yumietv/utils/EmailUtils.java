package cn.yumietv.utils;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtils {
    public static void sendEmail(String email, String code, String yt) throws Exception {
        Properties prop = new Properties();
        prop.put("mail.host", "smtp.163.com");
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.auth", true);
        //使用java发送邮件5步骤
        //1.创建sesssion
        Session session = Session.getInstance(prop);
        //开启session的调试模式，可以查看当前邮件发送状态
        session.setDebug(true);

        //2.通过session获取Transport对象（发送邮件的核心API）
        Transport ts = session.getTransport();
        //3.通过邮件用户名密码链接
        ts.connect("yumietv@163.com", "qwer1234");

        //4.创建邮件
        //Message msg=createSimpleMail(session);

        //创建邮件对象
        MimeMessage mm = new MimeMessage(session);
        //设置发件人
        mm.setFrom(new InternetAddress("yumietv@163.com"));
        //设置收件人
        mm.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        //设置抄送人
        //mm.setRecipient(Message.RecipientType.CC, new InternetAddress("yumietv官方"));

        if (yt.equals("register")) {
            mm.setSubject("注册验证码");
            mm.setContent("非常感谢您对小站的支持_(:зゝ∠)_,您的验证码是:" + code + ",祝您使用愉快~", "text/html;charset=utf-8");
        }
        if (yt.equals("zhmm")) {
            mm.setSubject("找回密码");
            mm.setContent("以后要妥善管理您的密码哦(╥╯^╰╥),您的验证码是:" + code + ",祝您使用愉快~", "text/html;charset=utf-8");
        }

        //5.发送电子邮件
        ts.sendMessage(mm, mm.getAllRecipients());
    }

    public static boolean isEmail(String str) {
        // 要验证的字符串
        // 邮箱验证规则
        String regEx = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.matches();
        return rs;
    }
}
