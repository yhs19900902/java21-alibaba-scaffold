package com.yhs.database.handler;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.yhs.base.constant.CommonConstant;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.util.ClassUtils;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

/**
 * @author 07664-linwei
 * @version Id: DateMetaObjectHandler.java, v 0.1 2022/4/20 10:07 lw Exp $
 */
public class DateMetaObjectHandler implements MetaObjectHandler {

    /**
     * 填充值，先判断是否有手动设置，优先手动设置的值，例如：job必须手动设置
     *
     * @param fieldName  属性名
     * @param fieldVal   属性值
     * @param metaObject MetaObject
     * @param isCover    是否覆盖原有值,避免更新操作手动入参
     */
    private static void fillValIfNullByName(String fieldName, Object fieldVal, MetaObject metaObject, boolean isCover) {
        // 如果填充值为空
        if (fieldVal == null) {
            return;
        }
        // 没有 set 方法
        if (!metaObject.hasSetter(fieldName)) {
            return;
        }
        // 如果有手动设置的值
        Object userSetValue = metaObject.getValue(fieldName);
        String setValueStr = StrUtil.str(userSetValue, Charset.defaultCharset());
        if (StrUtil.isNotBlank(setValueStr) && !isCover) {
            return;
        }
        //  类型相同时设置
        Class<?> getterType = metaObject.getGetterType(fieldName);
        if (ClassUtils.isAssignableValue(getterType, fieldVal)) {
            metaObject.setValue(fieldName, fieldVal);
        }
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();

        // 审计字段自动填充
        fillValIfNullByName("createdTime", now, metaObject, false);
        fillValIfNullByName("createdDate", now, metaObject, false);
        // 删除标记自动填充
        fillValIfNullByName("delFlag", CommonConstant.STATUS_NORMAL, metaObject, false);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        fillValIfNullByName("updatedTime", LocalDateTime.now(), metaObject, true);
        fillValIfNullByName("updatedDate", LocalDateTime.now(), metaObject, true);
    }
}
