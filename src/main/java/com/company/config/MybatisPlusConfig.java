package com.company.config;

import org.mybatis.spring.annotation.MapperScan;

import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages={"com.company.modules.*.dao"})
public class MybatisPlusConfig {


}
