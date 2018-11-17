--数据库初始化脚本
CREATE DATABASE seckill;

use seckill;

CREATE TABLE seckill(
	`seckill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
	`name` varchar(120) NOT NULL COMMENT '商品名称',
	`number` int NOT NULL COMMENT '库存数量',
	`start_time` timestamp NOT NULL COMMENT '秒杀开启时间',
	`end_time` timestamp NOT NULL COMMENT '秒杀结束时间',
	`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	PRIMARY KEY(seckill_id),
	key idx_start_time(start_time),
	key idx_end_time(end_time),
	key idx_create_time(create_time)
)ENGINE = InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';


insert into seckill(name,number,start_time,end_time)
	values
		('1000元秒杀iphone6',100,'2018-11-13 00:00:00','2018-11-14 00:00:00'),
		('6000元秒杀iphoneX',100,'2018-11-09 00:00:00','2018-11-10 00:00:00'),
		('100元秒杀小米',100,'2018-11-09 00:00:00','2018-11-10 00:00:00'),
		('10000元秒杀外星人',100,'2018-11-09 00:00:00','2018-11-11 00:00:00');
		
CREATE TABLE success_killed(
	`seckill_id` bigint NOT NULL COMMENT '商品库存id',
	`user_phone` bigint NOT NULL COMMENT '用户手机号',
	`state` tinyint NOT NULL DEFAULT -1 COMMENT '状态表示：-1是无效 0是成功',
	`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
PRIMARY KEY(`seckill_id`,`user_phone`),
key  idx_create_time(create_time)
)ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

mysql -u root 
