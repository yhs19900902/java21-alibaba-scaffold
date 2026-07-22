package com.demo.test.pojo.vo.response;

import com.yhs.base.pojo.vo.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author 03952-yehuasheng
 * @version Id: ProductBusinessInfoResponseVO.java, v0.1 2023/9/21 08:23 yehuasheng Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductBusinessInfoResponseVO extends BaseVO {
    private String model;
    private long quantity;
    private String sort;
    private BigDecimal price;
    private BigDecimal priceWithoutTax;
    private BigDecimal priceWithTax;
    private BigDecimal total;
    private BigDecimal historyLowestPrice;
    private BigDecimal longTermFixedPrice;
    private BigDecimal amountDiscountRate;
    private BigDecimal levelDiscountRate;
    private BigDecimal codeLevelDiscountRate;
    private BigDecimal fineTuningsRate;
    private BigDecimal totalDiscount;
    private BigDecimal virtualDiscountRate;
    private BigDecimal settlementDiscountRate;
    private int customerSettlement;
    private boolean haveDelivery;
    private int delivery;
    private String msg;
    private String errorType;
    private int maxQuantity;
    private String code;
    private String unit;
    private String productName;
    private boolean standard;
}
