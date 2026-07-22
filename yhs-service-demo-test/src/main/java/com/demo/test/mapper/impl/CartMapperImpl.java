package com.demo.test.mapper.impl;

import com.demo.test.dao.CartDAO;
import com.demo.test.mapper.CartMapper;
import com.demo.test.pojo.po.UserInfoPO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 03952-yehuasheng
 * @version Id: CartMapperImpl.java, v0.1 2023/9/14 12:12 yehuasheng Exp $
 */
@Service
public class CartMapperImpl extends MPJBaseServiceImpl<CartDAO, UserInfoPO> implements CartMapper {
}
