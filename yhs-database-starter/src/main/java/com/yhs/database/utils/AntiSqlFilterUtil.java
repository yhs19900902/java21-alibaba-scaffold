package com.yhs.database.utils;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author 07664-linwei
 * @version Id: AntiSqlFilterUtils.java, v 0.1 2022/4/21 14:47 lw Exp $
 */
@UtilityClass
public class AntiSqlFilterUtil {


    private static final String[] KEY_WORDS = {"master", "truncate", "insert", "select", "delete", "update", "declare",
            "alter", "drop", "sleep"};


    public static List<OrderItem> getOrderItem(String[] ascs, String[] descs) {

        List<OrderItem> orderItemList = new ArrayList<>();
        Optional.ofNullable(ascs).ifPresent(s -> orderItemList.addAll(
                Arrays.stream(s).filter(sqlInjectPredicate()).map(OrderItem::asc).collect(Collectors.toList())));
        Optional.ofNullable(descs).ifPresent(s -> orderItemList.addAll(
                Arrays.stream(s).filter(sqlInjectPredicate()).map(OrderItem::desc).collect(Collectors.toList())));
        return orderItemList;
    }


    /**
     * 判断用户输入里面有没有关键字
     *
     * @return Predicate
     */
    private static Predicate<String> sqlInjectPredicate() {
        return sql -> {
            for (String keyword : KEY_WORDS) {
                if (CharSequenceUtil.containsIgnoreCase(sql, keyword)) {
                    return false;
                }
            }
            return true;
        };
    }

}
