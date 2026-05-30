package com.lifepulse.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Knife4j在线接口文档配置
 * 访问地址：http://localhost:8080/doc.html
 */
@Configuration
public class Knife4jConfig {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info();
        info.setTitle("LifePulse后端接口文档");
        info.setVersion("1.0.0");
        info.setDescription("LifePulse - 一站式本地生活服务平台后端API");

        // 2. 构建 OpenAPI 对象
        return new OpenAPI()
                .info(info)
                .servers(Collections.singletonList(
                        new Server().url(contextPath).description("后端服务地址")
                ));
    }
}
