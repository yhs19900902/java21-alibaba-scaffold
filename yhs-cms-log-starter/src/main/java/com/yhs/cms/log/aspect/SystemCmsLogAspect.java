package com.yhs.cms.log.aspect;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yhs.base.constant.CommonConstant;
import com.yhs.base.exception.BizException;
import com.yhs.base.pojo.po.BasePO;
import com.yhs.base.pojo.vo.BaseVO;
import com.yhs.base.pojo.vo.BusinessResponse;
import com.yhs.base.utils.LogUtil;
import com.yhs.base.utils.UUIDUtil;
import com.yhs.base.utils.WebUtil;
import com.yhs.cms.log.annotate.SystemCmsLog;
import com.yhs.cms.log.annotate.Tables;
import com.yhs.cms.log.enums.ResultCodeEnum;
import com.yhs.cms.log.enums.SystemCmsLogTypeEnum;
import com.yhs.cms.log.kafka.producer.SystemCmsLogProducer;
import com.yhs.cms.log.pojo.po.CmsLogPO;
import com.yhs.cms.log.properties.SecurityClassProperties;
import com.yhs.cms.log.utils.BaseUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 03952-yehuasheng
 * @version Id: SystemCmsLogAspect.java, v0.1 2024/11/18 15:25 yehuasheng Exp $
 */
@Aspect
@Component
public class SystemCmsLogAspect {
    /**
     * 日志
     */
    public static final Logger logger = LogUtil.getLogger(SystemCmsLogAspect.class.getName());
    private static final String UPDATED_DATE_PARAMS = "updatedDateTime";
    /**
     * 日志发生生产者
     */
    @Resource
    private SystemCmsLogProducer systemCmsLogProducer;
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private SecurityClassProperties securityClassProperties;

    @Pointcut("@annotation(com.yhs.cms.log.annotate.SystemCmsLog)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object logPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("access log aspect ");

        // 设置参数
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SystemCmsLog systemCmsLog = method.getAnnotation(SystemCmsLog.class);
        Tables[] table = systemCmsLog.tables();
        CmsLogPO cmsLogPO = setLogData(joinPoint, table, systemCmsLog);

        // 判断是否被更新/删除
        if (StringUtils.isNotBlank(cmsLogPO.getOriginalData()) && !updatedTimeCheck(table[0].tableName(), cmsLogPO.getOriginalData(), joinPoint.getArgs())) {
            cmsLogPO.setResult(CommonConstant.FALSE_VALUE);
            cmsLogPO.setResultData(JSON.toJSONString(BusinessResponse.fail(ResultCodeEnum.PLEASE_REFRESH_THE_PAGE.getCode(), ResultCodeEnum.PLEASE_REFRESH_THE_PAGE.getDescription())));
            // 异步推送
            systemCmsLogProducer.sendMessage(BaseUtil.kafkaKey(cmsLogPO), JSON.toJSONString(cmsLogPO));

            throw new BizException(ResultCodeEnum.PLEASE_REFRESH_THE_PAGE.getCode(), ResultCodeEnum.PLEASE_REFRESH_THE_PAGE.getDescription());
        }

        // 执行程序
        Object proceed = joinPoint.proceed();

        try {
            BusinessResponse businessResponse = JSON.parseObject(JSON.toJSONString(proceed), BusinessResponse.class);
            cmsLogPO.setResult(businessResponse.success() ? CommonConstant.TRUE_VALUE : CommonConstant.FALSE_VALUE);
            cmsLogPO.setResultData(JSON.toJSONString(businessResponse));
            systemCmsLogProducer.sendMessage(BaseUtil.kafkaKey(cmsLogPO), JSON.toJSONString(cmsLogPO));
        } catch (Exception e) {
            logger.error("get logEntity fail . errorMsg {}", e.toString());
        }
        return proceed;
    }

    private CmsLogPO setLogData(ProceedingJoinPoint joinPoint, Tables[] table, SystemCmsLog systemCmsLog) {
        CmsLogPO cmsLogPO = CmsLogPO.builder().build();
        try {
            cmsLogPO.setCreatedDay(LocalDate.now());
            cmsLogPO.setId(UUIDUtil.getStringUUID());
            cmsLogPO.setCreatedDateTime(LocalDateTime.now());
            cmsLogPO.setCreatedBy(getUserName());

            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes(), "只能在Spring Web环境使用@SysLog记录日志")).getRequest();
            cmsLogPO.setOperationTable(systemCmsLog.tables()[0].tableName());
            cmsLogPO.setUri(request.getRequestURI());
            cmsLogPO.setIp(WebUtil.getIP(request));
            cmsLogPO.setUa(CharSequenceUtil.sub(request.getHeader("user-agent"), 0, 500));
            cmsLogPO.setOperationModule(joinPoint.getTarget().getClass().getName() + CommonConstant.DOT + joinPoint.getSignature().getName());
            cmsLogPO.setOperationData(JSON.toJSONString(joinPoint.getArgs()));
            cmsLogPO.setLogType(systemCmsLog.type().getType());
            if (StringUtils.equals(SystemCmsLogTypeEnum.UPDATED.getType(), systemCmsLog.type().getType())
                    || StringUtils.equals(SystemCmsLogTypeEnum.DELETED.getType(), systemCmsLog.type().getType())) {
                //更新前数据
                String originDataString = updatedDeal(table, joinPoint.getArgs());
                cmsLogPO.setOriginalData(originDataString);
            }
            return cmsLogPO;
        } catch (Exception e) {
            return cmsLogPO;
        }
    }

    private String updatedDeal(Tables[] tables, Object[] args) {
        try {
            Map<String, Object> mapMap = new HashMap<>(4);
            for (Tables table : tables) {
                BaseMapper<Object> mapperBean = (BaseMapper<Object>) applicationContext.getBean(table.dao());

                String tableName = table.tableName();
                String sql = setWhereSql(table.fields(), args);
                if (StringUtils.isBlank(sql)) {
                    if (table.nameSake()) {
                        String randomStr = RandomStringUtils.randomAlphanumeric(2).toUpperCase();
                        tableName = tableName + "_" + randomStr;
                    }
                    mapMap.put(tableName, "{}");
                } else {
                    // 查数据库
                    Object objectResult = mapperBean.selectOne(new LambdaQueryWrapper<>().apply(sql));
                    Map factory = new HashMap<>();
                    Optional.ofNullable(objectResult).ifPresent(o -> factory.putAll(JSON.parseObject(JSON.toJSONString(o), Map.class)));

                    if (table.nameSake()) {
                        String randomStr = RandomStringUtils.randomAlphanumeric(2).toUpperCase();
                        tableName = tableName + "_" + randomStr;
                    }
                    mapMap.put(tableName, factory);
                }
            }

            return JSON.toJSONString(mapMap);
        } catch (Exception e) {
            logger.error("get original data fail. error:{}", e.getMessage());
            return CommonConstant.EMPTY;
        }
    }

    /**
     * 设置查询sql
     *
     * @param fields 字段
     * @param args   值
     * @return String
     */
    private String setWhereSql(Tables.Field[] fields, Object[] args) {
        Optional<Map> pojo = Arrays.stream(args)
                .filter(f -> f instanceof BaseVO || f instanceof BasePO)
                .map(m -> JSON.parseObject(JSON.toJSONString(m), Map.class)).findFirst();
        if (pojo.isPresent()) {
            String whereSql = "1=1 %s ";
            Map map = pojo.get();
            String collect = Arrays.stream(fields)
                    .map(m -> {
                        List<String> list = null;
                        // 是否存在别名
                        if (m.isAlias()) {
                            // 是否为in
                            if (m.isMany()) {
                                Object o = map.get(m.alias());
                                if (o instanceof List) {
                                    list = JSONArray.parseArray(JSONObject.toJSONString(o), String.class);
                                }
                                return " and " + BaseUtil.camelToUnderline(m.property()) + " in (" +
                                        list.stream().map(s -> "\'" + s + "\'").collect(Collectors.joining(",")) + ")";
                            }
                            return " and " + BaseUtil.camelToUnderline(m.property()) + "='" + map.get(m.alias()) + "'";
                        } else {
                            if (m.property().contains("::")) {
                                return " and " + BaseUtil.camelToUnderline(m.property().split("::")[0]) + "='" + map.get(m.property().split("::")[1]) + "'";
                            } else {
                                return " and " + BaseUtil.camelToUnderline(m.property()) + "='" + map.get(m.property()) + "'";
                            }
                        }
                    })
                    .collect(Collectors.joining(" "));
            return String.format(whereSql, collect);
        } else {
            return CommonConstant.EMPTY;
        }
    }

    private boolean updatedTimeCheck(String tableName, String originDataString, Object[] args) {
        Map map = (Map) JSON.parseObject(originDataString, Map.class).get(tableName);
        if (map != null) {
            //数据库查询出来的更新时间
            Object dataBaseTime = map.get(UPDATED_DATE_PARAMS);
            if (dataBaseTime == null) {
                return true;
            }

            //获取当前入参的更新时间
            Optional<Map> pojo = Arrays.stream(args)
                    .filter(f -> f instanceof BaseVO || f instanceof BasePO)
                    .map(m -> JSON.parseObject(JSON.toJSONString(m), Map.class)).findFirst();
            if (pojo.isPresent()) {
                Object paramsTime = pojo.get().get(UPDATED_DATE_PARAMS);
                if (paramsTime == null) {
                    return false;
                }
                return !LocalDateTime.parse(dataBaseTime.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).isAfter(LocalDateTime.parse(paramsTime.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        }
        return true;
    }

    private String getUserName() throws ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {
        Class<?> aClass = Class.forName(securityClassProperties.getClassz());
        Method method = aClass.getMethod(securityClassProperties.getMethod());
        Object invoke = method.invoke(aClass);
        return invoke.toString();
    }
}
