create database IF NOT EXISTS db_420100;
create database IF NOT EXISTS db_420200;


use db_420100;
create table t_user
(
    `id`     bigint(20) NOT NULL AUTO_INCREMENT,
    `name`   varchar(50),
    `create_time` datetime,
    `age` INT,
    `snow_id` bigint(20),
    `city_code` varchar(50),
    PRIMARY KEY (`id`)
);

create table t_order_202407
(
    `id`     bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` bigint(20),
    `order_num`  bigint(20),
    `create_time` datetime,
    `city_code` varchar(50),
    PRIMARY KEY (`id`)
);

create table t_order_202408
(
    `id`     bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` bigint(20),
    `order_num`  bigint(20),
    `create_time` datetime,
    `city_code` varchar(50),
    PRIMARY KEY (`id`)
);

use db_420200;
create table t_user
(
    `id`     bigint(20) NOT NULL AUTO_INCREMENT,
    `name`   varchar(50),
    `create_time` datetime,
    `age` INT,
    `snow_id` bigint(20),
    `city_code` varchar(50),
    PRIMARY KEY (`id`)
);

create table t_order_202407
(
    `id`     bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` bigint(20),
    `order_num`  bigint(20),
    `create_time` datetime,
    `city_code` varchar(50),
    PRIMARY KEY (`id`)
);

create table t_order_202408
(
    `id`     bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` bigint(20),
    `order_num`  bigint(20),
    `create_time` datetime,
    `city_code` varchar(50),
    PRIMARY KEY (`id`)
);