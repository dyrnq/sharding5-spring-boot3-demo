# sharding5-spring-boot3-demo
sharding5 spring-boot3分表分库demo


  基础数据按用户所在城市分库，业务数据按时间按月分表。此方案适用单表业务数据每月在2000W以下的应用。需提前创建几年的表,分表curd操作时必须指定开始时间和结束时间，不然会增加查询范围。
