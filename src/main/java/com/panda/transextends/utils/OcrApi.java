package com.panda.transextends.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.transextends.pojo.OcrEntity;
import jdk.nashorn.internal.objects.annotations.Constructor;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Component
public class OcrApi {
    @Value("${ocr.host}")
    private String host;

    @Value("${ocr.port}")
    private int port;

    private Set<Map.Entry<String, String>> entries;

    @PostConstruct
    private void init() {
        try {
            URL resource = Thread.currentThread().getContextClassLoader().getResource("ocr_langs.ini");
            Ini ini = new Ini();
            ini.load(resource);
            // 读取 system
            Profile.Section section = ini.get("langs");
            entries = section.entrySet();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String convert2ocrLang(String srcLang) {
        for (Map.Entry<String, String> entry : entries) {
            if (srcLang.equals(entry.getKey())) return entry.getValue();
        }
        return "";
    }

    public List<OcrEntity> extract(String srcLang, String srcFile) {
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
                JSONObject jsonObject = JSONObject.parseObject(response.toString());
                int code = jsonObject.getIntValue("code");
                if (code == 200) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    List<OcrEntity> ocrEntities = data.toJavaList(OcrEntity.class);
                    return ocrEntities;
                }
                return null;
            }

        } catch (Exception e) {
            System.out.println("请求异常");
            throw new RuntimeException(e);
        }
    }
}
