package com.demo.test.pojo.vo.response;

import com.yhs.base.pojo.vo.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author 03952-yehuasheng
 * @version Id: TestResponseVO.java, v0.1 2023/9/14 10:43 yehuasheng Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "测试返回参数")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestResponseVO extends BaseVO {
    @Schema(name = "desc", description = "简介", example = "您的名字是：xxx， 年龄是：xxx")
    private String desc;
}
