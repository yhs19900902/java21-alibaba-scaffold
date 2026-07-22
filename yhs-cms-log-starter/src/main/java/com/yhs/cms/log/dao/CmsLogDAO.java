package com.yhs.cms.log.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yhs.cms.log.pojo.po.CmsLogPO;
import org.springframework.stereotype.Repository;

/**
 * @author 03952-yehuasheng
 * @version Id: CmsLogDAO.java, v0.1 2024/11/21 08:21 yehuasheng Exp $
 */
@Repository("systemCmsLog")
@DS("systemCmsLog")
public interface CmsLogDAO extends BaseMapper<CmsLogPO> {
}
