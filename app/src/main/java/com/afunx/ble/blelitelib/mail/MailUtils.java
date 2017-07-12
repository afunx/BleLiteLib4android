package com.afunx.ble.blelitelib.mail;

import android.os.Environment;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by afunx on 11/07/2017.
 */

public class MailUtils {
    /**
     * 检测邮箱地址是否合法
     *
     * @param address
     * @return true合法 false不合法
     */
    private static boolean verifyEmailAddress(String address) {
        if (null == address || "".equals(address))
            return false;

        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
        Matcher m = p.matcher(address);
        return m.matches();
    }

    public static boolean sendEmail(String from, String fromPwd, String to, String subject, String message) {
        if (!verifyEmailAddress(from) || !verifyEmailAddress(to)) {
            System.out.println("enter verifyEmailAddress");
            return false;
        }

        try {
            // Create the email message
            MultiPartEmail email = new MultiPartEmail();
            email.setDebug(true);
            // 这里使用163邮箱服务器，实际需要修改为对应邮箱服务器
            //smtp.163.com:25   smtp.qq.com:587
            email.setHostName("smtp.163.com");
            email.setSmtpPort(25);//163邮箱25
            email.setSocketTimeout(6 * 1000);
            email.setCharset("UTF-8");
            email.setStartTLSEnabled(true);
            email.setAuthentication(from, fromPwd);
            email.addTo(to, to);
            email.setFrom(from, from);
            email.setSubject(subject);
            email.setMsg(message);

            String sendStr = email.send();
            System.out.println("sendStr=" + sendStr);
        } catch (EmailException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 只是用来参考的DEMO
     *
     * @throws EmailException
     * @Description TODO 发送带附件的email
     */
    private static void sendEmailByApacheCommonsEmail(String from, String fromPwd, String to, String cc, String bcc, String subject, String message)
            throws EmailException {

        if (!verifyEmailAddress(from) || !verifyEmailAddress(to)) {
            System.out.println("enter verifyEmailAddress");
            return;
        }

        // Create the email message
        MultiPartEmail email = new MultiPartEmail();
        email.setDebug(true);
        // 这里使用163邮箱服务器，实际需要修改为对应邮箱服务器
        //smtp.163.com:25   smtp.qq.com:587
        email.setHostName("smtp.qq.com");
        email.setSmtpPort(456);//163邮箱25
        email.setSocketTimeout(6 * 1000);
        email.setCharset("UTF-8");
//        email.setTLS(true);
        email.setStartTLSEnabled(true);
//        email.setSSL(true);
        email.setAuthentication(from, fromPwd);
        email.addTo(to, to);
//        email.addBcc(bcc);
//        email.addCc(cc);
        email.setFrom(from, from);
        email.setSubject(subject);
        email.setMsg(message);

//        // Create the attachment
//        EmailAttachment attachment2 = new EmailAttachment();
//        attachment2.setPath(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "pdf02.png");
//        attachment2.setDisposition(EmailAttachment.ATTACHMENT);
//        attachment2.setDescription("pdf02");
//        attachment2.setName("pdf02.png");
//
//        EmailAttachment attachment1 = new EmailAttachment();
//        attachment1.setPath(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "pdf01.png");
//        attachment1.setDisposition(EmailAttachment.ATTACHMENT);
//        attachment1.setDescription("pdf01");
//        attachment1.setName("pdf01.png");
//
//        email.attach(attachment1);
//        email.attach(attachment2);

        // send the email
        String sendStr = email.send();
        System.out.println("sendStr=" + sendStr);
    }

}
