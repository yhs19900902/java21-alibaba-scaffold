package com.demo2.test2.mapper.impl;

import com.demo2.test2.dao.CartDAO;
import com.demo2.test2.mapper.CartMapper;
import com.demo2.test2.pojo.po.UserInfoPO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 03952-yehuasheng
 * @version Id: CartMapperImpl.java, v0.1 2023/9/14 12:12 yehuasheng Exp $
 */
@Service
public class CartMapperImpl extends MPJBaseServiceImpl<CartDAO, UserInfoPO> implements CartMapper {
}
