package com.serezka.jpt.api;

import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@PropertySource("classpath:gpt.properties")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GPTApi {
    Gson gson = new Gson();

    public void query(List<String> messages, double temperature) throws IOException {
        Query query = new Query(messages.stream().map(Query.Message::new).toList(), temperature);
        String qjson = gson.toJson(query);
        HttpPost httpPost = getHttpPost("", getStringEntity(qjson));

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = httpClient.execute(httpPost);
        String result = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
        Root root = gson.fromJson(result, Root.class);
        System.out.println(root.getAnswer());
    }

    @NotNull
    private static StringEntity getStringEntity(String val) throws UnsupportedEncodingException {
        StringEntity stringEntity = new StringEntity(val);
        stringEntity.setContentType("application/json");
        return stringEntity;
    }

    @NotNull
    private static HttpPost getHttpPost(String url, StringEntity params) {
        HttpPost request = new HttpPost(url);
        request.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        request.addHeader("Accept-Language", "ru-RU,ru");
        request.addHeader("Connection", "keep-alive");
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Origin", "http://121.166.113.241:37702");
        request.addHeader("Referer", "http://121.166.113.241:37702/");
        request.addHeader("Sec-GPC", "1");
        request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        request.addHeader("X-Requested-With", "XMLHttpRequest");
        request.setEntity(params);
        return request;
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class Root {
        String answer;
        String model;
        int promptTokens;
        String promptPrice;
        int completionTokens;
        String completionPrice;
        int totalTokens;
        String totalPrice;
    }

    @AllArgsConstructor
    public static class Query {
        List<Message> msg;
        String radio = "gpt-4";
        double temperature;

        public Query(List<Message> msg, double temperature) {
            this.msg = msg;
            this.temperature = temperature;
        }

        @AllArgsConstructor
        public static class Message {
            String content;
            String role = "user";

            public Message(String content) {
                this.content = content;
            }
        }
    }
}
