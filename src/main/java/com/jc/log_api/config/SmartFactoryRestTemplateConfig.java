package com.jc.log_api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class SmartFactoryRestTemplateConfig {

    @Bean
    public RestTemplate smartFactoryRestTemplate() {

        // 🔹 응답 바디를 여러 번 읽을 수 있도록 BufferingClientHttpRequestFactory 사용
        BufferingClientHttpRequestFactory requestFactory =
                new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());

        RestTemplate rt = new RestTemplate(requestFactory);

        // 요청/응답 로깅 인터셉터 등록
        rt.getInterceptors().add(loggingInterceptor());

        return rt;
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {

            // ===== Request Log =====
            log.debug("\n====================== SmartFactory API REQUEST ======================");
            log.debug("URL      : {}", request.getURI());
            log.debug("Method   : {}", request.getMethod());
            log.debug("Headers  : {}", request.getHeaders());
            if (body.length > 0) {
                log.debug("Body     : {}", new String(body, StandardCharsets.UTF_8));
            }

            ClientHttpResponse response = execution.execute(request, body);

            // 🔹 BufferingClientHttpRequestFactory 덕분에 바디를 복사해서 읽어도
            //    RestTemplate 내부에서 다시 읽을 수 있음
            byte[] responseBody = StreamUtils.copyToByteArray(response.getBody());
            String responseText = new String(responseBody, StandardCharsets.UTF_8);

            log.debug("====================== SmartFactory API RESPONSE ======================");
            log.debug("Status   : {} {}", response.getStatusCode(), response.getStatusText());
            log.debug("Body     : {}", responseText);

            // response 그대로 반환
            return response;
        };
    }
}
