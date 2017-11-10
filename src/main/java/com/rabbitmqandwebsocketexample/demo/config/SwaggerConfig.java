package com.rabbitmqandwebsocketexample.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    //完成配置类的书写后我们已经可以直接访问我们的文档了，启动项目，
    //访问 http://localhost:9090/swagger-ui.html

    /**
     * 不过没有中文注释的接口始终对于用户不太友好，
     * 我们接下来的工作就是给每一个接口添加注释。
     * 在Controller中我们使用@ApiOperation注解来注释我们接口的一些基本信息，
     * 使用@ApiImplicitParams，@ApiImplicitParam注解来注释我们的入參信息。
     * @return
     */
    @Bean
    public Docket createApi(){
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.rabbitmqandwebsocketexample.demo.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(){
        Contact contact = new Contact("FJY","http://blog.csdn.net/qq_31001665","1807401232@qq.com");
        return new ApiInfoBuilder()
                .title("API文档")
                .description("API使用即参数定义")
                .termsOfServiceUrl("http://blog.csdn.net/qq_31001665")
                .contact(contact)
                .version("1.1.0")
                .build();
    }
}
