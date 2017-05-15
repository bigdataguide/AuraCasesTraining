# AuraWeb

# 1. 描述
AuraWeb是AuraAnalysis的Web端项目，通过各种可视化方式展示网站统计指标以及相关机器学习结果。

# 2. 目录说明
AuraWeb用到了Spring, Struts2, MyBatis等Web开发框架，以及前端JS库d3和echarts  
目录结构如下:  
src/main/resource: 存放工程相关配置文件  
src/main/webapp: 存放前端JS，CSS和JSP代码  
src/main/scala: 主要的业务逻辑  
target: 存放编译好之后的代码 

# 3. 部署说明
AuraWeb可以AuraAnalysis独立部署，AuraAnalysis分析网站访问日志，生成统计数据存入到数据库中。AuraWeb从数据库中读取相关数据展示到页面上。  
## 2.1 环境设置
编辑/etc/hosts, 增加一行：  
<ip> bigdata  
## Tomcat部署
从官网上下载apache-tomcat-7.0.70包，解压到本地目录，并设置TOMCAT_HOME

# 4. 运行程序
## 4.1 编译war包
mvn clean package war:war  
在target目录下会生成AuraWeb.war
## 4.2 启动Tomcat
将AuraWeb.war拷贝到$TOMCAT_HOME/webapps目录下  
运行$TOMCAT_HOME/bin/startup.sh  
打开浏览器访问http://localhost:8080/AuraWeb/  

# FAQ