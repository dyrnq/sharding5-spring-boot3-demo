/**
 * @author zhanggzheng
 */

package com.company.modules.demo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Date;

@Schema(description = "用户信息")
@Data
public class UserInfoVo {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "用户名")
    @NotEmpty(message = "userName 不能为空")
    private String userName;

    @Schema(description = "年龄")
    @Min(value = 10, message = "age 必须大于10")
    private Integer age;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @NotEmpty(message = "city 不能为空")
    private String city;

}
