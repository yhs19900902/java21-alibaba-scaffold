package com.yhs.knife4j.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import com.yhs.base.constant.CommonConstant;
import com.yhs.knife4j.core.common.ExecutorEnum;
import com.yhs.knife4j.core.common.RouteUtils;
import com.yhs.knife4j.core.executor.ApacheClientExecutor;
import com.yhs.knife4j.core.executor.OkHttpClientExecutor;
import com.yhs.knife4j.core.pojo.BasicAuth;
import com.yhs.knife4j.core.pojo.HeaderWrapper;
import com.yhs.knife4j.core.pojo.SwaggerRoute;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 07664-linwei
 * @version Id: RouteDispatcher.java, v 0.1 2023/7/25 9:12 lw Exp $
 */
public class RouteDispatcher {

    /**
     * 请求头
     */
    public static final String ROUTE_PROXY_HEADER_NAME = "knfie4j-gateway-request";
    public static final String ROUTE_PROXY_HEADER_BASIC_NAME = "knife4j-gateway-basic-request";
    public static final String OPENAPI_GROUP_ENDPOINT = "/swagger-resources";
    public static final String OPENAPI_GROUP_INSTANCE_ENDPOINT = "/swagger-instance";
    public static final String ROUTE_BASE_PATH = "/";
    /**
     * 当前项目的contextPath
     */
    private final String rootPath;
    private final RouteRepository routeRepository;
    private final RouteCache<String, SwaggerRoute> routeCache;
    private final Set<String> ignoreHeaders = new HashSet<>();
    Logger logger = LoggerFactory.getLogger(RouteDispatcher.class);
    private RouteExecutor routeExecutor;

    public RouteDispatcher(RouteRepository routeRepository, RouteCache<String, SwaggerRoute> routeRouteCache,
                           ExecutorEnum executorEnum, String rootPath) {
        this.routeRepository = routeRepository;
        this.routeCache = routeRouteCache;
        this.rootPath = rootPath;
        initExecutor(executorEnum);
        ignoreHeaders.addAll(Arrays.asList("host", "content-length", ROUTE_PROXY_HEADER_NAME, ROUTE_PROXY_HEADER_BASIC_NAME, "Request-Origion", "language", "knife4j-gateway-code"));
    }

    private void initExecutor(ExecutorEnum executorEnum) {
        if (executorEnum == null) {
            throw new IllegalArgumentException("ExecutorEnum can not be empty");
        }
        switch (executorEnum) {
            case APACHE:
                this.routeExecutor = new ApacheClientExecutor();
                break;
            case OKHTTP:
                this.routeExecutor = new OkHttpClientExecutor();
                break;
            default:
                throw new UnsupportedOperationException("UnSupported ExecutorType:" + executorEnum.name());
        }
    }

    public boolean checkRoute(String header) {
        if (CharSequenceUtil.isNotBlank(header)) {
            SwaggerRoute swaggerRoute = routeRepository.getRoute(header);
            if (swaggerRoute != null) {
                return CharSequenceUtil.isNotBlank(swaggerRoute.getUri());
            }
        }
        return false;
    }

    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try {
            RouteRequestContext routeContext = new RouteRequestContext();
            this.buildContext(routeContext, request);
            RouteResponse routeResponse = routeExecutor.executor(routeContext);
            writeResponseStatus(routeResponse, response);
            writeResponseHeader(routeResponse, response);
            writeBody(routeResponse, response);
        } catch (Exception e) {
            logger.error("has Error:{}", e.getMessage());
            logger.error(e.getMessage(), e);
            //write Default
            writeDefault(request, response, e.getMessage());
        }
    }

    protected void writeDefault(HttpServletRequest request, HttpServletResponse response, String errMsg) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter printWriter = response.getWriter();
            Map<String, String> map = new HashMap<>();
            map.put("message", errMsg);
            map.put("code", "500");
            map.put("path", request.getRequestURI());
            new JSONObject(map).write(printWriter);
            printWriter.close();
        } catch (IOException e) {
            //ignore
        }
    }

    /**
     * Write 响应状态码
     *
     * @param routeResponse routeResponse
     * @param response      response
     */
    protected void writeResponseStatus(RouteResponse routeResponse, HttpServletResponse response) {
        if (routeResponse != null) {
            response.setStatus(routeResponse.getStatusCode());
        }
    }

    /**
     * Write响应头
     *
     * @param routeResponse route响应对象
     * @param response      响应response
     */
    protected void writeResponseHeader(RouteResponse routeResponse, HttpServletResponse response) {
        if (routeResponse != null) {
            if (CollUtil.isNotEmpty(routeResponse.getHeaders())) {
                for (HeaderWrapper header : routeResponse.getHeaders()) {
                    if (!CharSequenceUtil.equalsIgnoreCase(header.getName(), "Transfer-Encoding")) {
                        response.addHeader(header.getName(), header.getValue());
                    }
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("响应类型:{},响应编码:{}", routeResponse.getContentType(), routeResponse.getCharsetEncoding());
            }
            response.setContentType(routeResponse.getContentType());
            if (routeResponse.getContentLength() > 0) {
                response.setContentLengthLong(routeResponse.getContentLength());
            }
            response.setCharacterEncoding(routeResponse.getCharsetEncoding().displayName());
        }
    }

    /**
     * 响应内容
     *
     * @param routeResponse route响应对象
     * @param response      响应对象
     */
    protected void writeBody(RouteResponse routeResponse, HttpServletResponse response) throws IOException {
        if (routeResponse != null) {
            if (routeResponse.success()) {
                InputStream inputStream = routeResponse.getBody();
                if (inputStream != null) {
                    int read = -1;
                    byte[] bytes = new byte[1024 * 1024];
                    ServletOutputStream outputStream = response.getOutputStream();
                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }
                    IoUtil.close(inputStream);
                    IoUtil.close(outputStream);
                }
            } else {
                String text = routeResponse.text();
                if (CharSequenceUtil.isNotBlank(text)) {
                    PrintWriter printWriter = response.getWriter();
                    printWriter.write(text);
                    printWriter.close();
                }
            }

        }
    }

    /**
     * 构建路由的请求上下文
     *
     * @param routeRequestContext 请求上下文
     * @param request             请求对象
     */
    protected void buildContext(RouteRequestContext routeRequestContext, HttpServletRequest request) throws IOException {
        //当前请求是否basic请求
        String basicHeader = request.getHeader(ROUTE_PROXY_HEADER_BASIC_NAME);
        if (CharSequenceUtil.isNotBlank(basicHeader)) {
            BasicAuth basicAuth = routeRepository.getAuth(basicHeader);
            if (basicAuth != null) {
                //增加Basic请求头
                routeRequestContext.addHeader("Authorization", RouteUtils.authorize(basicAuth.getUsername(),
                        basicAuth.getPassword()));
            }
        }
        String version = null;
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = request.getHeader(key);
            if (CommonConstant.VERSION.equals(key)) {
                version = value;
            }
            if (!ignoreHeaders.contains(key.toLowerCase())) {
                routeRequestContext.addHeader(key, value);
            }

        }
        SwaggerRoute swaggerRoute = getRoute(request.getHeader(ROUTE_PROXY_HEADER_NAME), version);
        //String uri="http://knife4j.xiaominfo.com";
        String uri = swaggerRoute.getUri();
        if (CharSequenceUtil.isBlank(uri)) {
            throw new RuntimeException("Uri is Empty");
        }
        String host = URI.create(uri).getHost();
        String fromUri = request.getRequestURI();
        StringBuilder requestUrlBuilder = new StringBuilder();
        requestUrlBuilder.append(uri);
        //判断当前聚合项目的contextPath
        if (CharSequenceUtil.isNotBlank(this.rootPath) && !CharSequenceUtil.equals(this.rootPath, ROUTE_BASE_PATH)) {
            fromUri = fromUri.replaceFirst(this.rootPath, "");
            //此处需要追加一个请求头basePath，因为父项目设置了context-path
            routeRequestContext.addHeader("X-Forwarded-Prefix", this.rootPath);
        }
        //判断servicePath
        if (CharSequenceUtil.isNotBlank(swaggerRoute.getServicePath()) && !CharSequenceUtil.equals(swaggerRoute.getServicePath(),
                ROUTE_BASE_PATH)) {
            if (CharSequenceUtil.startWith(fromUri, swaggerRoute.getServicePath())) {
                //实际在请求时,剔除servicePath,否则会造成404
                fromUri = fromUri.replaceFirst(swaggerRoute.getServicePath(), "");
            }
        }
        requestUrlBuilder.append(fromUri);
        //String requestUrl=uri+fromUri;
        String requestUrl = requestUrlBuilder.toString();
        if (logger.isDebugEnabled()) {
            logger.debug("目标请求Url:{},请求类型:{},Host:{}", requestUrl, request.getMethod(), host);
        }
        routeRequestContext.setOriginalUri(fromUri);
        routeRequestContext.setUrl(requestUrl);
        routeRequestContext.setMethod(request.getMethod());

        routeRequestContext.addHeader("Host", host);
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String name = params.nextElement();
            String value = request.getParameter(name);
            //logger.info("param-name:{},value:{}",name,value);
            routeRequestContext.addParam(name, value);
        }
        //增加文件，sinc 2.0.9
        String contentType = request.getContentType();
        if ((!CharSequenceUtil.isEmpty(contentType)) &&
                contentType.contains("multipart/form-data")) {
            try {
                Collection<Part> parts = request.getParts();
                if (CollUtil.isNotEmpty(parts)) {
                    Map<String, String> paramMap = routeRequestContext.getParams();
                    parts.forEach(part -> {
                        String key = part.getName();
                        if (!paramMap.containsKey(key)) {
                            routeRequestContext.addPart(part);
                        }
                    });
                }
            } catch (ServletException e) {
                //ignore
                logger.warn("get part error,message:{}", e.getMessage());
            }
        }
        routeRequestContext.setRequestContent(request.getInputStream());
    }

    public SwaggerRoute getRoute(String header) {
        //去除缓存机制，由于Eureka以及Nacos设立了心跳检测机制，服务在多节点部署时，节点ip可能存在变化,导致调试最终转发给已经下线的服务
        //since 2.0.9
        SwaggerRoute swaggerRoute = routeRepository.getRoute(header);
        return swaggerRoute;
    }

    public SwaggerRoute getRoute(String header, String version) {
        SwaggerRoute route = getRoute(header);
        String name = route.getName();
        List<SwaggerRoute> routes = routeRepository.getRoutesAll();
        Map<String, List<SwaggerRoute>> collect = routes.stream().
                collect(Collectors.groupingBy(SwaggerRoute::getName));
        List<SwaggerRoute> serviceInstanceList = collect.get(name);
        if (CharSequenceUtil.isNotBlank(version)) {
            serviceInstanceList = serviceInstanceList.stream().
                    filter(swaggerRoute -> version.equals(swaggerRoute.getVersion())).collect(Collectors.toList());
        }
        if (CollUtil.isEmpty(serviceInstanceList)) {
            return route;
        }
        return RandomUtil.randomEle(serviceInstanceList);
    }

    public List<SwaggerRoute> getRoutes() {
        return routeRepository.getRoutes();
    }

    public String getRootPath() {
        return rootPath;
    }
}
