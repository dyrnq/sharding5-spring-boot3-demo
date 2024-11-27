/**
 * @author zhangzheng
 */

package com.company.modules.demo.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.company.common.api.vo.Result;
import com.company.common.utils.TimeUtils;
import com.company.modules.demo.dao.OrderDao;
import com.company.modules.demo.dao.UserInfoDao;
import com.company.modules.demo.model.Order;
import com.company.modules.demo.model.UserInfo;
import com.company.modules.demo.vo.UserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Tag(name = "用户管理")
@Slf4j
@RestController
@RequestMapping("demo")
public class DemoController {

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private OrderDao orderDao;

    @Operation(summary = "获取用户列表", method = "GET", description = "获取用户列表")
    @GetMapping("getUserList")
    public Result<List<UserInfo>> getUserList() {
        UserInfo user = new UserInfo();
        user.setName("张三");
        user.setId(1L);
        user.setCreateTime(new Date());

        List<UserInfo> list = new ArrayList<>();
        list.add(user);

        Result<List<UserInfo>> result = new Result<>();
        result.setMessage("操作成功");
        result.setResult(list);

        return result;
    }


    @Parameters
    @Operation(summary = "查询单个用户", method = "GET", description = "根据用户id查询用户信息")
    @GetMapping("user/{id}")
    public Result<List<UserInfo>> getUserById(@PathVariable("id") @Schema(description = "用户id") Integer id) {
        log.info("查询用户，id：{}", id);

        Date startTime = TimeUtils.string2Date("2024-07-01");
        Date endTime = new Date();

        List<Order> orderList = orderDao.selectList(new QueryWrapper<Order>().eq("city_code", "420200").between("create_time",startTime,endTime));
        System.out.println(orderList.size());

        List<UserInfo> userInfoList = userInfoDao.selectList(new QueryWrapper<UserInfo>().eq("city_code", "420200"));

        Result<List<UserInfo>> result = new Result<List<UserInfo>>();
        result.setMessage("操作成功");
        result.setResult(userInfoList);

        return result;
    }


    @Operation(summary = "创建用户", method = "POST", description = "创建用户")
    @PostMapping("add")
    public Result<?> add(@RequestBody @Validated UserInfoVo user) {
        UserInfo userInfo1 = new UserInfo(null, user.getUserName(), new Date(), 1, 1L, "420100");
        userInfoDao.insert(userInfo1);

        UserInfo userInfo2 = new UserInfo(null, user.getUserName(), new Date(), 1, 1L, "420200");
        userInfoDao.insert(userInfo2);

        String time1 = "2024-08-01";
        Order order1 = new Order(null, 1L, TimeUtils.string2Date(time1), 1L, "420200");
        orderDao.insert(order1);

        String time2 = "2024-07-01";
        Order order2 = new Order(null, 1L, TimeUtils.string2Date(time2), 1L, "420100");
        orderDao.insert(order2);

        return Result.OK();
    }


}
