package com.panda.transextends;


import com.panda.transextends.pojo.OcrEntity;
import com.panda.transextends.service.impl.TransDocxImpl;
import com.panda.transextends.service.impl.TransImagesImpl;
import com.panda.transextends.service.impl.TransTikaImpl;
import com.panda.transextends.utils.CoreApi;
import com.panda.transextends.utils.OcrApi;
import com.panda.transextends.utils.RedisUtil;
import com.panda.transextends.utils.WordConvert;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@SpringBootTest
class TransExtendsApplicationTests {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    TransTikaImpl transTika;

    @Autowired
    OcrApi ocrApi;

    @Autowired
    TransDocxImpl transDocx;


    @Test
    void wordTestConvert() {
        final String s = WordConvert.convertToSimplifiedChinese("今天天氣真好呀,good morning");
        System.out.println(s);
    }

    @Test
    void removeAllPics() throws IOException {
        transDocx.removeAllPictures("/Users/baiyang/Desktop/a.docx");
    }

    @Test
    void contextLoads() throws Exception {
//        transTika.translate(70, "Chinese", "", "/Users/baiyang/Desktop/翻译资源/a.docx", "/Users/baiyang/Desktop/翻译资源/aa123.docx");
        List<OcrEntity> lines = ocrApi.extract("Chinese", "/Users/baiyang/Desktop/翻译资源/bbb.jpg");
        for (OcrEntity line : lines) {
            System.out.println(line.toString());
        }
    }

    @Test
    void extractContent() throws IOException {
        File file = new File("/Users/baiyang/Desktop/bbbx.pdf");
        PDDocument document = PDDocument.load(file);
        //Instantiate PDFTextStripper class
        PDFTextStripper pdfStripper = new PDFTextStripper();
        //Retrieving text from PDF document
        String text = pdfStripper.getText(document);
        System.out.println(text);
        //Closing the document
        document.close();
    }

    @Test
    void testNewPage() throws IOException {
        PDDocument document = new PDDocument();
        for (int i = 0; i < 10; i++) {
            //Creating a blank page
            PDPage blankPage = new PDPage();
            //Adding the blank page to the document
            document.addPage(blankPage);
        }
        //Saving the document
        document.save("/Users/baiyang/Desktop/1.pdf");
        System.out.println("PDF created");
        //Closing the document
        document.close();
    }

    @Test
    void testPdfAddText() throws IOException {
        //Loading an existing document
        File file = new File("/Users/baiyang/Desktop/text.pdf");
        PDDocument doc = PDDocument.load(file);
        //Creating a PDF Document
        PDPage page = doc.getPage(1);
        PDPageContentStream contentStream = new PDPageContentStream(doc, page);
        //Begin the Content stream
        contentStream.beginText();
        //Setting the font to the Content stream
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 16);
        //Setting the leading
        contentStream.setLeading(14.5f);
        //Setting the position for the line
        contentStream.newLineAtOffset(25, 725);
        String text1 = "This is an example of adding text to a page in the pdf document.";
        String text2 = "as we want like this using the ShowText()  method of the ContentStream class";
        contentStream.showText(text1);
        contentStream.endText();
        contentStream.close();
        //Saving the document
        doc.save(new File("/Users/baiyang/Desktop/text2.pdf"));
        //Closing the document
        doc.close();
    }

    @Test
    void testEml() {
        String strFile = "/Users/baiyang/Desktop/翻译资源/eml/pa.eml";
        String desFile = "/Users/baiyang/Desktop/翻译资源/eml/pa_out.eml";
        String dir = "/Users/baiyang/Desktop/翻译资源/eml/";
        try (InputStream inputStream = new FileInputStream(strFile)) {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage msg = new MimeMessage(session, inputStream);
            MimeMessageParser parser = new MimeMessageParser(msg).parse();
            //邮件唯一id
            String messageId = parser.getMimeMessage().getMessageID();
            System.out.println(messageId);
            //发件人
            String from = parser.getFrom();
            System.out.println(from);
            //收件人列表
            List<Address> toArray = parser.getTo();
            System.out.println(toArray);
            //抄送人列表
            List<Address> ccArray = parser.getCc();
            System.out.println(ccArray);
            //密送人列表
            List<Address> bccArray = parser.getBcc();
            System.out.println(bccArray);
            //邮件发送时间
            Date sendDate = parser.getMimeMessage().getSentDate();
            System.out.println(sendDate);
            //邮件主题
            String subject = parser.getSubject();
            System.out.println(subject);
            //获取正文
            String html = parser.getHtmlContent();
            List<DataSource> dataSources = parser.getAttachmentList();
            //获取不到html内容时，则获取非html文本内容
            if (html == null || html.length() == 0) {
                String plain = parser.getPlainContent();
                plain = "中国";
                msg.setContent(plain, "text/plain; charset=utf-8");
            } else {
                //解析html 并翻译
                Document document = Jsoup.parse(html);
                Element entry = document.select("body").first();
                Elements tags = entry.getAllElements();
                for (Element tag : tags) {
                    for (Node child : tag.childNodes()) {
                        if (child instanceof TextNode && !((TextNode) child).isBlank()) {
                            ((TextNode) child).text("中国人"); //replace to word
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
            FileOutputStream fileOutputStream = new FileOutputStream(desFile);
            msg.writeTo(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    void testEmlOld() {
        String strFile = "/Users/baiyang/Desktop/翻译资源/eml/aa.eml";
        String desFile = "/Users/baiyang/Desktop/翻译资源/eml/aa_out.eml";
        String dir = "/Users/baiyang/Desktop/翻译资源/eml/";
        try (InputStream inputStream = new FileInputStream(strFile)) {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage msg = new MimeMessage(session, inputStream);
            MimeMessageParser parser = new MimeMessageParser(msg).parse();
            //邮件唯一id
            String messageId = parser.getMimeMessage().getMessageID();
            System.out.println(messageId);
            //发件人
            String from = parser.getFrom();
            System.out.println(from);
            //收件人列表
            List<Address> toArray = parser.getTo();
            System.out.println(toArray);
            //抄送人列表
            List<Address> ccArray = parser.getCc();
            System.out.println(ccArray);
            //密送人列表
            List<Address> bccArray = parser.getBcc();
            System.out.println(bccArray);
            //邮件发送时间
            Date sendDate = parser.getMimeMessage().getSentDate();
            System.out.println(sendDate);
            //邮件主题
            String subject = parser.getSubject();
            System.out.println(subject);
            //获取正文
            String html = parser.getHtmlContent();
            System.out.println(html);
            //所有文件，包括附件和正文中的图片等文件
            List<DataSource> dataSources = parser.getAttachmentList();
            //获取不到html内容时，则获取非html文本内容
            if (html == null || html.length() == 0) {
                String plain = parser.getPlainContent();
                System.out.println(plain);
                msg.setContent(plain, "text/plain");
            } else {
                //获取正文中的图片等文件
                Collection<String> contentIds = parser.getContentIds();
                for (String contentId : contentIds) {
                    DataSource contentFile = parser.findAttachmentByCid(contentId);
                    dataSources.remove(contentFile);
                    String name = contentFile.getName();
                    InputStream is = contentFile.getInputStream();
                    FileUtils.copyInputStreamToFile(is, new File(dir + name));
                    html = html.replace("cid:" + contentId, name);
                }
                msg.setContent(html, "text/html");
                FileUtils.writeStringToFile(new File(dir + "test.html"), html, Charset.defaultCharset());
            }
            for (DataSource dataSource : dataSources) {
                String name = dataSource.getName();
                System.out.println(name);
                InputStream is = dataSource.getInputStream();
                FileUtils.copyInputStreamToFile(is, new File(dir + name));
            }
            msg.saveChanges();
            FileOutputStream fileOutputStream = new FileOutputStream(desFile);
            msg.writeTo(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
