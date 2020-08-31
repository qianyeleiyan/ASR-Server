package org.codesdream.asr.configure;

import com.google.common.collect.Sets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Swagger2配置类
 */
@Configuration
@EnableSwagger2
public class ASRSwaggerConfigure {
    @Bean
    public Docket createRestApi() {

        List<Parameter> pars = new ArrayList<Parameter>();

        pars.add(new ParameterBuilder().name("openid").description("账号openid").hidden(true).order(1)
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).defaultValue("24310760d8bb8b6542e5a3f16a0d67253214e01ee7ab0e96a1").build());
        pars.add(new ParameterBuilder().name("signed").description("客户端签名").hidden(true).order(2)
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).defaultValue("6d4923fca4dcb51f67b85e54a23a8d763d9e02af").build());
        pars.add(new ParameterBuilder().name("timestamp").description("时间戳").hidden(true).order(3)
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).defaultValue(Long.toString(new Date().getTime())).build());

        return new Docket(DocumentationType.SWAGGER_2)
                .protocols(Sets.newHashSet("http"))
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.codesdream.asr.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("自动日程规划服务端接口定义")
                .version("0.0.1")
                .description("用于对服务端接口进行说明")
                .build();
    }
}
