package com.panda.transextends.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class OcrApi {
    @Value("${ocr.host}")
    private String host;

    @Value("${ocr.port}")
    private int port;

    public String convert2ocrLang(String srcLang) {
        return "";
    }

    public boolean detected(String srcLang, String srcFile) {
        try {
            String reqUrl = String.format("http://%s:%s/extract", host, port);
            URL url = new URL(reqUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            Map map = new HashMap<>();
            String lang = convert2ocrLang(srcLang);
            map.put("lang", lang);
            map.put("src_file", srcFile);
            try (OutputStream os = con.getOutputStream()) {
                byte[] bytes = JSONObject.toJSONBytes(map);
                os.write(bytes, 0, bytes.length);
            }
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                con.disconnect();
                JSONObject jsonObject = JSONObject.parseObject(response.toString());
                int code = jsonObject.getIntValue("code");
                if (code == 200) {
                    return true;
                }
                return false;
            }

        } catch (Exception e) {
            System.out.println("请求异常");
            throw new RuntimeException(e);
        }
    }
}
