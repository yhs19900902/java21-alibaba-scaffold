package com.yhs.springdoc.executor;

import org.springframework.beans.factory.SmartInitializingSingleton;

/**
 * @author 07664-linwei
 * @version Id: DocSpringExecutor.java, v 0.1 2023/7/27 10:21 lw Exp $
 */
public class DocSpringExecutor extends DocExecutor implements SmartInitializingSingleton {


    @Override
    public void afterSingletonsInstantiated() {
        super.start();
    }
}
