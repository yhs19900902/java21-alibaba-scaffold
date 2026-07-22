package com.yhs.springdoc.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: SpringDocProperties.java, v 0.1 2022/4/30 8:54 lw Exp $
 */
@Data
@ConfigurationProperties(prefix = "yhs.doc")
public class SpringDocProperties {

    /**
     * 标题
     **/
    private String title = "";

    /**
     * 描述
     **/
    private String description = "";

    /**
     * 版本
     **/
    private String version = "3.0.1";

    /**
     * 许可证
     **/
    private String license = "";

    /**
     * 许可证URL
     **/
    private String licenseUrl = "";

    /**
     * 服务条款URL
     **/
    private String termsOfServiceUrl = "";

    /**
     * host信息
     **/
    private String host = "";

    private List<Headers> headers = new ArrayList<>();

    /**
     * 联系人信息
     */
    private Contact contact = new Contact();


    private Authorization authorization = new Authorization();

    private Aggregation aggregation = new Aggregation();

    @Data
    @NoArgsConstructor
    public static class Headers {
        /**
         * headers名称
         */
        private String headerName;
        /**
         * 请求参数描述
         */
        private String description;
        /**
         * 是否允许空值
         */
        private boolean allowEmptyValue;
        /**
         * 示例
         */
        private String example;
        /**
         * 是否必传
         */
        private boolean required;
    }

    @Data
    @NoArgsConstructor
    public static class Contact {

        /**
         * 联系人
         **/
        private String name = "";

        /**
         * 联系人url
         **/
        private String url = "";

        /**
         * 联系人email
         **/
        private String email = "";

    }

    @Data
    @NoArgsConstructor
    public static class Authorization {
        /**
         * OAuth 模式
         */
        private GrantType grantType = GrantType.AUTHORIZATIONCODE;
        /**
         * 授权地址
         */
        private String authorizationUrl = null;
        /**
         *
         */
        private String tokenUrl = null;
        /**
         * 刷新token 地址
         */
        private String refreshUrl = null;
        /**
         * 作用域
         */
        private List<String> scopes = Arrays.asList("read", "write");

        /**
         * Gets or Sets in
         */
        public enum GrantType {
            IMPLICIT("简易模式"),
            PASSWORD("密码模式"),
            AUTHORIZATIONCODE("授权码模式");


            private final String value;

            GrantType(String value) {
                this.value = value;
            }

            @Override
            public String toString() {
                return String.valueOf(value);
            }
        }

        //   private String
    }

    @Data
    @NoArgsConstructor
    public static class Aggregation {

        private String groupName;

        private String uri;

        private String location = "/v3/api-docs";
        ;

        private String address;
    }
}
