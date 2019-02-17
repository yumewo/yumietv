package cn.yumietv.web.util;

/**
 * @Auther: yumie
 * @Date: 2019/2/5 13:59
 * @Description:
 */

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class SendMailBySSL {
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private static String smtpServer = "smtp.163.com"; // SMTP服务器地址
    private static String port = "465"; // 端口
    private static String username = "yumietv@163.com"; // 登录SMTP服务器的用户名
    private static String password = "qwer1234"; // 登录SMTP服务器的密码
    private static List<String> recipients = new ArrayList<String>(); // 收件人地址集合
    //private String subject; // 邮件主题
    //private static String content; // 邮件正文
    //private static String gs; // 用途
    private static List<String> attachmentNames = new ArrayList<String>(); // 附件路径信息集合

    /**
     * 进行base64加密，防止中文乱码
     */
    public static String changeEncode(String str) {
        try {
            str = MimeUtility.encodeText(new String(str.getBytes(), "UTF-8"),
                    "UTF-8", "B"); // "B"代表Base64
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 正式发邮件
     */
    public static boolean sendMail(String email, String code, String gs) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpServer);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.class", SSL_FACTORY);  //使用JSSE的SSL socketfactory来取代默认的socketfactory
        properties.put("mail.smtp.socketFactory.fallback", "false");  // 只处理SSL的连接,对于非SSL的连接不做处理

        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.socketFactory.port", port);

        Session session = Session.getInstance(properties);
        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);

        try {
            // 发件人
            Address address = new InternetAddress(username);
            message.setFrom(address);

            // 收件人
            System.out.println("收件人：" + email);
            Address toAddress = new InternetAddress(email);
            message.setRecipient(MimeMessage.RecipientType.TO, toAddress); // 设置收件人,并设置其接收类型为TO
            /**
             * TO：代表有健的主要接收者。 CC：代表有健的抄送接收者。 BCC：代表邮件的暗送接收者。
             * */

            // 时间
            message.setSentDate(new Date());

            Multipart multipart = new MimeMultipart();
            // 添加文本
            BodyPart text = new MimeBodyPart();
            if (gs.equals("register")) {
                // 主题
                message.setSubject(changeEncode("注册验证码"));
                text.setText("非常感谢您对小站的支持_(:зゝ∠)_,您的验证码是:" + code + ",祝您使用愉快~");
            }
            if (gs.equals("zhmm")) {
                message.setSubject(changeEncode("找回密码"));
                text.setText("以后要妥善管理您的密码哦(╥╯^╰╥),您的验证码是:" + code + ",祝您使用愉快~");
            }
            multipart.addBodyPart(text);
            if (attachmentNames != null && attachmentNames.size() > 0) {
                // 添加附件
                for (String fileName : attachmentNames) {
                    BodyPart adjunct = new MimeBodyPart();
                    FileDataSource fileDataSource = new FileDataSource(fileName);
                    adjunct.setDataHandler(new DataHandler(fileDataSource));
                    adjunct.setFileName(changeEncode(fileDataSource.getName()));
                    multipart.addBodyPart(adjunct);
                }
                attachmentNames.clear();
            }
            // 清空收件人集合，附件集合
            recipients.clear();
//            attachmentNames.clear();

            message.setContent(multipart);
            message.saveChanges();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            Transport transport = session.getTransport("smtp");
            transport.connect(smtpServer, username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

//    public static void main(String[] args) {
//        List<String> recipients = new ArrayList<String>();
////		recipients.add("123456789@qq.com");
//        recipients.add("1084460097@qq.com");
//        String subject = "这封邮件是为了测试SMTP的SSL加密传输";
//        String content = "这是这封邮件的正文";
//        List<String> attachmentNames = new ArrayList<String>();
////        attachmentNames.add("C://Users//Administrator//Desktop//kali.txt");
//        SendMailBySSL sendMailBySSL = new SendMailBySSL("smtp.163.com", "465",
//                "yumietv@163.com", "qwer1234", recipients, content,
//                attachmentNames,"register");
//        sendMailBySSL.sendMail();
//    }

}