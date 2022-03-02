package com.panda.transextends;

import com.panda.transextends.pojo.OcrEntity;
import com.panda.transextends.service.impl.TransImagesImpl;
import com.panda.transextends.service.impl.TransTikaImpl;
import com.panda.transextends.utils.CoreApi;
import com.panda.transextends.utils.OcrApi;
import com.panda.transextends.utils.RedisUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootTest
class TransExtendsApplicationTests {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    TransTikaImpl transTika;

    @Autowired
    OcrApi ocrApi;

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
        for (int i=0; i<10; i++) {
            //Creating a blank page
            PDPage blankPage = new PDPage();
            //Adding the blank page to the document
            document.addPage( blankPage );
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
        contentStream.setFont( PDType1Font.TIMES_ROMAN, 16);
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
}
