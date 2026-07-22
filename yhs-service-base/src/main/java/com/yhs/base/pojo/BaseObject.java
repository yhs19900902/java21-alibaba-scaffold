package com.yhs.base.pojo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author 03952-yehuasheng
 * @version Id: BaseObject.java, v0.1 2023/9/11 17:30 yehuasheng Exp $
 */
public abstract class BaseObject implements Serializable {
    /**
     * 序列化id
     */
    @Serial
    private static final long serialVersionUID = -5824534192273817261L;

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(this, object);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
