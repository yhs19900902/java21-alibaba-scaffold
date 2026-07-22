package com.demo.test.pojo.vo.request;

import com.yhs.base.pojo.vo.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * @author 03952-yehuasheng
 * @version Id: TestRequestVO.java, v0.1 2023/9/14 10:41 yehuasheng Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "测试")
public class TestRequestVO extends BaseVO {
    @NotBlank(message = "名字不能为空")
    @Schema(name = "name", description = "名字", example = "tom", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(name = "age", description = "年龄", example = "2")
    private int age;
}
