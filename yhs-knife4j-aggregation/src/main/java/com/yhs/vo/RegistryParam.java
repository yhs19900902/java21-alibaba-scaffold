package com.yhs.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 07664-linwei
 * @version Id: RegistryParam.java, v 0.1 2023/7/26 14:35 lw Exp $
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistryParam {

    @NotBlank(message = "group name is not null")
    private String groupName;
    @NotBlank(message = "uri is not null")
    private String uri;
    @NotBlank(message = "location is not null")
    private String location;

    private String version;

    private String token;
}
