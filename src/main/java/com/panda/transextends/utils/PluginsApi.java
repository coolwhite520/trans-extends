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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class PluginsApi {
    @Value("${plugins.host}")
    private String host;

    @Value("${plugins.port}")
    private int port;

    public boolean convert(String srcFile, String convertType) {
        String reqUrl = String.format("http://%s:%s/convert_file", host, port);
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            Map map = new HashMap<>();
            map.put("convert_type", convertType);
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
                String msg = jsonObject.getString("msg");
                String error = String.format("Child请求异常：URL->%s, ERR->%s", reqUrl, msg);
                throw new RuntimeException(error);
            }

        } catch (Exception e) {
            e.printStackTrace();
            String error = String.format("请求异常：URL->%s, ERR->%s", reqUrl, e);
            throw new RuntimeException(error);
        }
    }

    public boolean translate_email(int rowId, String srcLang, String desLang, String srcFile, String desFile) {
        String reqUrl = String.format("http://%s:%s/trans_file", host, port);
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            Map map = new HashMap<>();
            map.put("row_id", rowId);
            map.put("src_lang", srcLang);
            map.put("des_lang", desLang);
            map.put("src_file", srcFile);
            map.put("des_file", desFile);
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
                String msg = jsonObject.getString("msg");
                String error = String.format("Child请求异常：URL->%s, ERR->%s", reqUrl, msg);
                throw new RuntimeException(error);
            }

        } catch (Exception e) {
            e.printStackTrace();
            String error = String.format("请求异常：URL->%s, ERR->%s", reqUrl, e);
            throw new RuntimeException(error);
        }
    }
}
