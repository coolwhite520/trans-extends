package com.panda.transextends.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.transextends.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class CoreApi {
    @Value("${core.host}")
    private String host;

    @Value("${core.port}")
    private int port;

    @Autowired
    private RedisUtil redisUtil;

    private static String hmacSHA1Encrypt(String encryptText) throws Exception {
        String encryptKey = "Today I want to eat noodle.";
        byte[] data = encryptKey.getBytes("UTF-8");
        SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(secretKey);
        byte[] text = encryptText.getBytes("UTF-8");
        return Base64.getEncoder().encodeToString(mac.doFinal(text));
    }
    public ArrayList<List<String>> tokenize(String srcLang, String content) {
        String reqUrl = String.format("http://%s:%s/tokenize", host, port);
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            Map map = new HashMap<>();
            map.put("src_lang", srcLang);
            map.put("content", content);
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
                    JSONArray objects = jsonObject.getJSONArray("list");
                    ArrayList<List<String>> lists = new ArrayList<>();
                    for (int i = 0; i < objects.size(); i++) {
                        JSONArray jsonArray = objects.getJSONArray(i);
                        List<String> strings = jsonArray.toJavaList(String.class);
                        lists.add(strings);
                    }
                    return lists;
                }
                String msg = jsonObject.getString("msg");
                String error = String.format("Child请求异常：URL->%s, ERR->%s", reqUrl, msg);
                throw new RuntimeException(error);
            }

        } catch (Exception e) {
            String error = String.format("请求异常：URL->%s, ERR->%s", reqUrl, e);
            throw new RuntimeException(error);
        }
    }
    public String translate(String srcLang, String desLang, String content) {
        String reqUrl = String.format("http://%s:%s/translate", host, port);
        try {
            String keyStr = String.format("src_lang=%s&des_lang=%s&content=%s", srcLang, desLang, content);
            String key = hmacSHA1Encrypt(keyStr);
            if(redisUtil.exists(key)) {
                return redisUtil.get(key);
            }
            URL url = new URL(reqUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            Date date = new Date();
            long timestamp = date.getTime() / 1000;
            Map map = new HashMap<>();
            map.put("src_lang", srcLang);
            map.put("des_lang", desLang);
            map.put("content", content);
            map.put("timestamp", timestamp);
            String format = String.format("src_lang=%s&des_lang=%s&content=%s&timestamp=%s", srcLang, desLang, content, timestamp);
            String sign = hmacSHA1Encrypt(format);
            map.put("sign", sign);
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
                    String data = jsonObject.getString("data");
                    if(desLang.equals("Chinese")) {
                        // 由于翻译引擎的问题，导致有些翻译会出现繁体，故转换之
                        data =  WordConvert.convertToSimplifiedChinese(data);
                    }
                    redisUtil.set(key, data);
                    return data;
                }
                String msg = jsonObject.getString("msg");
                String error = String.format("Child请求异常：URL->%s, ERR->%s", reqUrl, msg);
                throw new RuntimeException(error);
            }

        } catch (Exception e) {
            String error = String.format("请求异常：URL->%s, ERR->%s", reqUrl, e);
            throw new RuntimeException(error);
        }
    }
}
