package com.demo.test.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yhs.base.pojo.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 03952-yehuasheng
 * @version Id: CartPO.java, v0.1 2023/9/14 12:07 yehuasheng Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("uc_user_info")
public class UserInfoPO extends BasePO {
    private String userCode;
    private String username;
    private String mobile;
    private String email;
}
