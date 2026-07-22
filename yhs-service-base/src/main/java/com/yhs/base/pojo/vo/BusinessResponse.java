package com.yhs.base.pojo.vo;

import com.yhs.base.constant.CommonConstant;
import com.yhs.base.enums.DefaultResponseEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author 03952-yehuasheng
 * @version Id: BusinessResponse.java, v0.1 2023/9/12 08:19 yehuasheng Exp $
 */
@Getter
@Schema(description = "响应实体")
public class BusinessResponse<T> implements Serializable {
    /**
     * 成功码
     */
    public static final int RESPONSE_OK = 200;
    /**
     * 服务异常码
     */
    public static final int RESPONSE_ERROR = 5000;
    /**
     * 序列化
     */
    @Serial
    private static final long serialVersionUID = -3824534192273817261L;
    /**
     * 响应状态码
     * 规则如下：
     * 第一位：标识错误的来源 标识业务场景
     * 第二位：标识错误的等级及致命程度 0-9 致命程度递增
     * 后续2位随机
     * 案例：
     * 200 成功
     * 1xxx 授权、权限、权益相关
     * 2xxx 值验证, 表单验证
     * 3xxx 系统内部, 逻辑处理异常的错误类型
     * 4xxx 网络请求, 白名单、限流相关的错误类型
     * 5xxx 自定义的错误类型
     * 6xxx 内部服务调用报错, 如rpc、跨模块、应用间调用报错
     * 7xxx 依赖第三方, 除去企业内部模块或应用的依赖第三方的请求错误
     * 8xxx 数据库执行的错误码
     * 9xxx 和前端的契约响应码  前端会根据此类响应码做特殊交互
     */
    @Schema(description = "响应状态码", example = "200")
    private int code;

    /**
     * 响应提示
     */
    @Schema(description = "响应提示", example = "success")
    private String message;

    /**
     * 响应业务数据
     */
    @Schema(description = "响应业务数据", example = "\"{\"data\":\"data\"}\"")
    private T data;

    public BusinessResponse() {
        this.code = RESPONSE_OK;
        this.message = CommonConstant.EMPTY;
    }

    /**
     * 响应成功
     *
     * @param data 响应体
     * @param <T>  T
     * @return BusinessResponse
     */
    public static <T> BusinessResponse<T> ok(T data) {
        BusinessResponse<T> businessResponse = new BusinessResponse<>();
        businessResponse.setCode(RESPONSE_OK);
        businessResponse.setMessage(CommonConstant.SUCCESS);
        businessResponse.setData(data);
        return businessResponse;
    }

    /**
     * 响应失败
     *
     * @param code    响应码
     * @param message 响应信息
     * @param data    响应体
     * @param <T>     T
     * @return BusinessResponse
     */
    public static <T> BusinessResponse<T> fail(int code, String message, T data) {
        BusinessResponse<T> businessResponse = new BusinessResponse<>();
        businessResponse.setCode(code);
        businessResponse.setMessage(message);
        businessResponse.setData(data);
        return businessResponse;
    }

    public static <T> BusinessResponse<T> fail(int code, String message) {
        return fail(code, message, null);
    }

    public static <T> BusinessResponse<T> fail(String message) {
        return fail(RESPONSE_ERROR, message, null);
    }

    public static <T> BusinessResponse<T> fail(DefaultResponseEnum defaultResponseEnum) {
        return fail(defaultResponseEnum.getCode(), defaultResponseEnum.getMessage());
    }

    /**
     * 判断返回响应是否成功
     *
     * @return boolean
     */
    public boolean success() {
        return code == RESPONSE_OK;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BusinessResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
