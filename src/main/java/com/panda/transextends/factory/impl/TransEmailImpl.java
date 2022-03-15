package com.panda.transextends.factory.impl;

import com.panda.transextends.mapper.RecordDAO;
import com.panda.transextends.factory.TransFile;
import com.panda.transextends.utils.CoreApi;
import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

@Component
public class TransEmailImpl implements TransFile {

    @Autowired
    CoreApi coreApi;

    @Autowired
    RecordDAO recordDAO;

    //            //邮件唯一id
//            String messageId = parser.getMimeMessage().getMessageID();
//            System.out.println(messageId);
//            //发件人
//            String from = parser.getFrom();
//            System.out.println(from);
//            //收件人列表
//            List<Address> toArray = parser.getTo();
//            System.out.println(toArray);
//            //抄送人列表
//            List<Address> ccArray = parser.getCc();
//            System.out.println(ccArray);
//            //密送人列表
//            List<Address> bccArray = parser.getBcc();
//            System.out.println(bccArray);
//            //邮件发送时间
//            Date sendDate = parser.getMimeMessage().getSentDate();
//            System.out.println(sendDate);
//            //邮件主题
//            String subject = parser.getSubject();
//            System.out.println(subject);

    public long calculateTotalProgress(String srcLang, String srcFile) throws Exception {
        long total = 0;
        try (InputStream is = new FileInputStream(srcFile)) {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage msg = new MimeMessage(session, is);
            MimeMessageParser parser = new MimeMessageParser(msg).parse();
            String html = parser.getHtmlContent();
            //获取不到html内容时，则获取非html文本内容
            if (html == null || html.length() == 0) {
                String plain = parser.getPlainContent();
                ArrayList<List<String>> list = coreApi.tokenize(srcLang, plain);
                for (List<String> strings : list) {
                    for (String string : strings) {
                        total++;
                    }
                }
            } else {
                //解析html 并翻译
                Document document = Jsoup.parse(html);
                Element entry = document.select("body").first();
                Elements tags = entry.getAllElements();
                for (Element tag : tags) {
                    for (Node child : tag.childNodes()) {
                        if (child instanceof TextNode && !((TextNode) child).isBlank()) {
                            total++;
                        }
                    }
                }
            }
            return total;
        }
    }

    @Override
    public boolean translate(long rowId, String srcLang, String desLang, String srcFile, String desFile) throws Exception {
        long total = calculateTotalProgress(srcLang, srcFile);
        long current = 0;
        int percent = 0;
        try (InputStream is = new FileInputStream(srcFile);
             FileOutputStream os = new FileOutputStream(desFile);
         ) {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage msg = new MimeMessage(session, is);
            MimeMessageParser parser = new MimeMessageParser(msg).parse();
            //获取正文
            String html = parser.getHtmlContent();
            List<DataSource> dataSources = parser.getAttachmentList();
            //获取不到html内容时，则获取非html文本内容
            if (html == null || html.length() == 0) {
                StringBuilder stringBuilder = new StringBuilder();
                String plain = parser.getPlainContent();
                ArrayList<List<String>> list = coreApi.tokenize(srcLang, plain);
                for (List<String> strings : list) {
                    for (String s : strings) {
                        String transContent = coreApi.translate(srcLang, desLang, s);
                        stringBuilder.append(transContent);
                        current++;
                        if (percent != 100 * current / total) {
                            percent = (int) (100 * current / total);
                            if (percent > 100) percent = 100;
                            recordDAO.updateProgress(rowId, percent);
                        }
                    }
                }
                msg.setContent(stringBuilder.toString(), "text/plain; charset=utf-8");
            } else {
                //解析html 并翻译
                Document document = Jsoup.parse(html);
                Element entry = document.select("body").first();
                Elements tags = entry.getAllElements();
                for (Element tag : tags) {
                    for (Node child : tag.childNodes()) {
                        if (child instanceof TextNode && !((TextNode) child).isBlank()) {
                            String text = ((TextNode) child).text();
                            text = coreApi.translate(srcLang, desLang, text);
                            ((TextNode) child).text(text); //replace to word
                            current++;
                            if (percent != 100 * current / total) {
                                percent = (int) (100 * current / total);
                                if (percent > 100) percent = 100;
                                recordDAO.updateProgress(rowId, percent);
                            }
                        }
                    }
                }
                Multipart multipart = new MimeMultipart("mixed");// mixed表示混合性，这里因为有文本，附件，所以是混合的。
                msg.setContent(multipart);

                BodyPart htmlPart = new MimeBodyPart();
                multipart.addBodyPart(htmlPart);

                Collection<String> contentIds = parser.getContentIds(); // 获取内嵌html的图片
                Multipart contentMulti;
                if (contentIds.size() > 0) {
                    contentMulti = new MimeMultipart("related");//这里的图片和文本是在一起显示的所以他们是关系型的。
                } else {
                    contentMulti = new MimeMultipart();
                }
                htmlPart.setContent(contentMulti);

                // 添加正文
                BodyPart textBody = new MimeBodyPart();
                contentMulti.addBodyPart(textBody);
                textBody.setContent(document.html(), "text/html; charset=utf-8");

                //添加html图片
                for (String contentId : contentIds) {
                    BodyPart jpgBody = new MimeBodyPart();
                    DataSource pic = parser.findAttachmentByCid(contentId);
                    jpgBody.setDataHandler(new DataHandler(pic));
                    jpgBody.setHeader("Content-ID", contentId);
                    contentMulti.addBodyPart(jpgBody);
                    dataSources.remove(pic);
                }

                // 添加附件
                for (DataSource source : dataSources) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.setDataHandler(new DataHandler(source));
                    attachmentPart.setFileName(source.getName());
                    multipart.addBodyPart(attachmentPart);
                }
            }
            msg.saveChanges();
            msg.writeTo(os);
            return true;
        }
    }
}
