CREATE TABLE `dianping`.`user`
(
    `id`        int(11) NOT NULL AUTO_INCREMENT,
    `create_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `telephone`  varchar(40) CHARACTER SET utf8 COLLATE utf8_unicode_ci  NOT NULL DEFAULT '',
    `password`  varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
    `nick_name` varchar(40) CHARACTER SET utf8 COLLATE utf8_unicode_ci  NOT NULL DEFAULT '',
    `gender`    int(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `telphone_unique_index`(`telphone`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;


CREATE TABLE `dianping`.`seller`
(
    `id`            int(0) NOT NULL AUTO_INCREMENT,
    `name`          varchar(80)   NOT NULL DEFAULT '',
    `created_at`    datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `remark_score`  decimal(2, 1) NOT NULL DEFAULT 0,
    `disabled_flag` decimal(0, 0) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

CREATE TABLE `dianping`.`category`
(
    `id`         int(0) NOT NULL AUTO_INCREMENT,
    `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `name`       varchar(20)  NOT NULL DEFAULT '',
    `icon_url`   varchar(200) NOT NULL DEFAULT '',
    `sort`       int(0) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `name_unique_in`(`name`) USING BTREE
)ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;



CREATE TABLE `dianping`.`shop`
(
    `id`            int(0) NOT NULL AUTO_INCREMENT,
    `created_at`    datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `name`          varchar(80)    NOT NULL DEFAULT '',
    `remark_score`  decimal(2, 1)  NOT NULL DEFAULT 0,
    `price_per_man` int(0) NOT NULL DEFAULT 0,
    `latitude`      decimal(10, 6) NOT NULL DEFAULT 0,
    `longitude`    decimal(10, 6) NOT NULL DEFAULT 0,
    `category_id`   int(0) NOT NULL DEFAULT 0,
    `tags`          varchar(1000)  NOT NULL DEFAULT '',
    `start_time`    varchar(200)   NOT NULL DEFAULT '',
    `end_time`      varchar(200)   NOT NULL DEFAULT '',
    `address`       varchar(200)   NOT NULL DEFAULT '',
    `seller_id`     int(0) NOT NULL DEFAULT 0,
    `icon_url`      varchar(100)   NOT NULL DEFAULT '',
    PRIMARY KEY (`id`)
)ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;