package com.demo.test.sao;

import com.alibaba.nacos.common.http.param.MediaType;
import com.demo.test.pojo.vo.request.ProductBusinessInfoRequestVO;
import com.demo.test.pojo.vo.response.ProductBusinessInfoResponseVO;
import com.demo.test.pojo.vo.response.YHDBusinessInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 03952-yehuasheng
 * @version Id: ProductBusinessInfoSAO.java, v0.1 2023/9/21 08:19 yehuasheng Exp $
 */
@FeignClient(name = "yhd-service-product-business-info", url = "http://10.11.0.83:9130")
public interface ProductBusinessInfoSAO {
    @PostMapping(value = "/product/v2/0/price", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    YHDBusinessInfoResponse<List<ProductBusinessInfoResponseVO>> getPrice(@RequestBody ProductBusinessInfoRequestVO productBusinessInfoRequestVO);
}
