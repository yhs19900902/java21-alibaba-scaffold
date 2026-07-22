package com.yhs.easyexcel.vo;

import com.alibaba.excel.enums.WriteDirectionEnum;
import lombok.Data;

/**
 * @author 07664-linwei
 * @version Id: FillData.java, v 0.1 2022/6/28 15:25 lw Exp $
 */
@Data
public class FillData {

    /**
     * 模板数据前缀 {前缀.}
     * 如果有多个list 必须有前缀
     */
    private String prefix;

    /**
     * 这里注意 入参用了forceNewRow 代表在写入list的时候不管list下面有没有空行 都会创建一行，然后下面的数据往后移动。默认 是false，会直接使用下一行，如果没有则创建。
     * forceNewRow 如果设置了true,有个缺点 就是他会把所有的数据都放到内存了，所以慎用
     * 简单的说 如果你的模板有list,且list不是最后一行，下面还有数据需要填充 就必须设置 forceNewRow=true 但是这个就会把所有数据放到内存 会很耗内存
     * 如果数据量大 list不是最后一行 参照下一个
     */
    private Boolean forceNewRow;


    /**
     * 水平写  WriteDirectionEnum.HORIZONTAL
     * 垂直写  WriteDirectionEnum.VERTICAL
     */
    private WriteDirectionEnum directionEnum;

    /**
     * 填充的数据
     */
    private Object data;

}
