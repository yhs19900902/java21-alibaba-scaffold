package com.yhs.log.event;

import com.yhs.base.pojo.dto.OptLogDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 07664-linwei
 * @version Id: SysLogEvent.java, v 0.1 2022/4/22 9:17 lw Exp $
 */
@Getter
@AllArgsConstructor
public class SysLogEvent {

    private OptLogDTO sysLog;
}
