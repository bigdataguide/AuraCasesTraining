#!/usr/bin/env bash

# 使用特权账号执行该指令, 将-u <user>和-p<password> 替换为对应的特权账户的用户名和密码
mysql -u root -proot -e "CREATE USER 'bigdata'@'%' IDENTIFIED BY 'bigdata';"
mysql -u root -proot -e "GRANT ALL PRIVILEGES ON aura.* TO 'bigdata'@'%';"

# 创建数据库和表
mysql -u bigdata -pbigdata < aura.sql
# 加载初始数据
mysql -u bigdata -pbigdata < aura_init.sql
