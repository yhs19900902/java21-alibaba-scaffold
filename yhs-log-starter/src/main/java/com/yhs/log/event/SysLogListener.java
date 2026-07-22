package com.yhs.log.event;

import com.yhs.base.pojo.dto.OptLogDTO;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import java.util.function.Consumer;

/**
 * @author 07664-linwei
 * @version Id: SysLogListener.java, v 0.1 2022/4/22 11:06 lw Exp $
 */
@AllArgsConstructor
public class SysLogListener {

    private final Consumer<OptLogDTO> consumer;

    @Async
    @Order
    @EventListener(SysLogEvent.class)
    public void saveSysLog(SysLogEvent event) {
        OptLogDTO logDTO = event.getSysLog();
        consumer.accept(logDTO);
    }


}
