<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!doctype html>
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>内存计算框架-Spark Core</title>
    
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="description" content="">
    <meta name="keywords" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
	
	<script type="text/javascript" src="pages/jquery2/jquery-2.2.3.min.js"></script>
	<script type="text/javascript" src="pages/echarts-3.2.1/echarts.min.js"></script>
	<script type="text/javascript" src="pages/echarts-3.2.1/china.js"></script>
	<script type="text/javascript" src="pages/echarts-3.2.1/world.js"></script>
	<script type="text/javascript" src="pages/js/echarts3-basic.js?v=1.1"></script>
	<script type="text/javascript" src="pages/js/memory.js?v=1.2"></script>
	
	<style type="text/css">
	#main {
		width: 100%;
		height: 584px;
	}
	body {
		margin: 0 0 0 0;
		background-image: url('pages/images/4.jpg');
		background-attachment:fixed;
		background-repeat:no-repeat;
		background-size:cover;
		-moz-background-size:cover;
		-webkit-background-size:cover;
	}
	.chart {
		
	}
	.marginRight {
		margin-right: 1px;
	}
	</style>

  </head>
  
  <body>
    <div id="chartFlow" style="width: 50%;float: left;margin-top:15px;" class="chart"></div>
    <div id="chartWorld" style="width: 49%;float: right;margin-top:15px;" class="chart marginRight"></div>
    <div id="chartSearch" style="width: 33%;float: left;margin-top:10px;" class="chart"></div>
    <div id="chartArea" style="width: 33%;float: left;margin-top:10px;" class="chart"></div>
    <div id="chartContent" style="width: 33%;float: right;margin-top:10px;" class="chart marginRight"></div>
    
    <script type="text/javascript">
    var height = $(window).height() / 2 - 15;
    $("#chartFlow").height(height);
    $("#chartWorld").height(height);
    $("#chartSearch").height(height);
    $("#chartArea").height(height);
    $("#chartContent").height(height);
    
    var chartFlow = echarts.init(document.getElementById('chartFlow'));
    var chartSearch = echarts.init(document.getElementById('chartSearch'));
    var chartArea = echarts.init(document.getElementById('chartArea'));
    var chartWorld = echarts.init(document.getElementById('chartWorld'));
    var chartContent = echarts.init(document.getElementById('chartContent'));

	function ajaxQuery() {
		/**
		 * 流量统计
		 */
		$.get({url:"common/memory_getMemoryList"}).done(function(data) {
			var days = [];
	    	var pvs = [];
	    	var uvs = [];
	    	var ips = [];
	    	var times = [];
	    	data.map(function(item) {
	    		days.push(item.day);
	    		pvs.push(item.pv);
	    		uvs.push(item.uv);
	    		ips.push(item.ip);
	    		times.push(item.time);
	    	})
	    	var types = ["页面浏览量","访问者数","访问IP数"]
	    	var datas = [pvs,uvs,ips];
	    	var option = getOptionFlow("流量统计", types, days, datas);
			chartFlow.setOption(option);
		});
		
		/**
		 * 搜索引擎
		 */
		$.get({url:"common/memory_getDimensionList?type=Search-engine"}).done(function(data) {
	    	data.map(function(item) {
	    		item.value = item.uv;
	    	})
	    	var option = getOptionSearch("搜索引擎", data);
			chartSearch.setOption(option);
		});
		
		/**
		 * 地区统计
		 */
	    $.get({url:"common/memory_getDimensionList?type=province"}).done(function(data) {
	    	var max = 0;
	    	data.map(function(item) {
	    		item.value = item.uv;
	    		if(max < item.value) {
	    			max = item.value;
	    		}
	    	})
	    	var option = getOptionArea("地区统计", data, max);
			chartArea.setOption(option);
		});
		
		/**
		 * 国家统计
		 */
	    $.get({url:"common/memory_getDimensionList?type=country"}).done(function(data) {
	    	var max = 0;
	    	data.map(function(item) {
	    		item.value = item.uv;
	    		if(max < item.value && item.name != '中国') {
	    			max = item.value;
	    		}
	    		if(item.name == '中国') {
	    			item.name = "-";
	    		}
	    	})
	    	var option = getOptionWorld("国家统计", data, max);
			chartWorld.setOption(option);
		});
		
		/**
		 * 新闻排行
		 */
		$.get({url:"common/memory_getMemoryContentList"}).done(function(data) {
			data = data.sort(function (a, b) {
		        return a.uv - b.uv;
		    })
			var titles = [];
			var uvs = [];
	    	data.map(function(item) {
	    		titles.push(subTitle(item.title, 22) + " : " + item.uv);
	    		uvs.push(item.uv);
	    	})
	    	var option = getOptionContent("新闻排行",titles,uvs);
			chartContent.setOption(option);
		});
	}
	function subTitle(title,length) {
		if(title.length > length) {
			return title.substring(0,length) + "...";
		}
		return title;
	}
	ajaxQuery();
    </script>
  </body>
</html>