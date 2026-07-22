package com.yhs.log.remote;


import com.yhs.base.pojo.dto.OptLogDTO;

/**
 * @author 07664-linwei
 * @version Id: RemoteSaveLogInfo.java, v 0.1 2022/6/1 14:17 lw Exp $
 */
public interface RemoteSaveLogInfoService {

    /**
     * 保存日志
     *
     * @param logDTO
     */
    void saveLog(OptLogDTO logDTO);
}
