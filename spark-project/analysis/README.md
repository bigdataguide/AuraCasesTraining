# AuraAnalysis

# 1. 描述
AuraAnalysis是一个网站指标统计系统，采用Spark技术实时分析网站访问日志，展示网站的整体访问的UV/PV，
访问流量的国家省份等地域分布信息，并采用机器学习技术预计访问的性别分布以及文章的类别分布。
通过实时分析，可以实时获取当前热门的浏览文章。

# 2. 目录说明
* data  
logs: 原始网站访问日志  
ml: 机器学习相关的训练数据  
model: 机器学习训练出来的模型数据
* deploy  
mysql: 数据表和初始化脚本  
kafka: kafka启动脚本和主题初始化脚本  
flume: flume启动脚本和数据导入脚本
* lib  
全部依赖的Jar包
* src
main/scala: spark代码  
main/resources/aura.properties 资源配置

# 3. 部署说明
拷贝deploy至/home/bigdata/Aura
```bash
mkdir -p /home/bigdata/Aura && cp -R deploy /home/bigdata/Aura/
```
## 3.1 环境设置
编辑/etc/sysconfig/network文件，更改HOSTNAME=bigdata  
编辑/etc/hosts, 增加一行：  
127.0.0.1 bigdata  
## 3.2 Mysql部署
安装启动Mysql
```bash
sudo yum install mysql
sudo service mysqld start
```
初始化Mysql数据库
```bash
cd /home/bigdata/Aura/deploy && bash create-table.sh
```
## 3.3 Kafka部署
从官网上下载kafka_2.11-0.10.1.0（其他版本也可以，但是需要修改对应的命令参数）  
解压到合适目录，将该目录设置为KAFKA_HOME  
将deploy/kafka/server.properties复制到$KAFKA_HOME/config/目录下  
将deploy/kafka/start-kafka.sh和deploy/kafka/start-zookeeper.sh复制到$KAFKA_HOME/bin目录下  
运行start-zookeeper.sh  
运行start-kafka.sh
运行create-topic.sh
## 3.4 Flume部署
从官网上下载apache-flume-1.7.0, 解压到合适目录，并将该目录设置为FLUME_HOME    
将deploy/flume/aura-kafka.properties拷贝到$FLUME_HOME/conf目录下   
将deploy/flume/start-flume.sh和deploy/flume/ingest-logs.sh拷贝到$FLUME_HOME/bin目录下   
运行start-flume.sh   
运行ingest-logs.sh   
查看消息内容   
```
bin/kafka-console-consumer.sh --zookeeper bigdata:2181 --from-beginning --topic aura --max-messages 10
```

# 4. 运行说明
本项目的程序可以直接在IDE中运行, 如果IDE所在的机器和服务部署的机器不一致，则需要在IDE机器中增加hosts映射
编辑/etc/hosts, 增加一行(将<ip>替换为服务部署机器的IP)
<ip> bigdata

批处理分析：
src/main/scala/com/aura/mllib/ChannelModel.scala 频道模型训练
src/main/scala/com/aura/mllib/GenderModel.scala 性别模型训练
src/main/scala/com/aura/util/AllAnalysis.scala 核心分析

流式分析
src/main/scala/com/aura/spark/streaming/StreamingAnalysis.scala

# 5. FAQ
1. 运行完程序之后，我如何查看结果？  
所有的运行结果都保存在数据库中，可以通过AuraWeb来图形化展示
2. 我设置好了Kafka，但是运行Spark程序的时候报错，不能连接到broker或者连接被关闭
在本机telnet bigdata 9092，如果无法连接，则说明远程kafka broker网络设置有问题，需要检查kafka是否设置了绑定地址
