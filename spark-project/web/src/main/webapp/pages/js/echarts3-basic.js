var TerrenCharts = {};
TerrenCharts.echarts = function(chartsConfig) {
	this.tooltip = {
        trigger: 'axis'
    }
	this.toolbox = {
	    show: true,
	    feature: {
	        saveAsImage: {}
	    }
    }
	this.xAxis = {
        type: 'category',
        boundaryGap: false // 边界间隙
    }
    this.yAxis = {
        type: 'value'
    }
	this.legend;
    this.series;
    this.radar;
    this.visualMap;
}
TerrenCharts.echarts.prototype = {
    initCharts: function(chartsConfig) {
		// 空值验证
	    if(chartsConfig == undefined) {
			return ;
		}
		// 数据验证
		if(!chartsConfig.chartsData) {
	    	chartsConfig.chartsData = this.getchartsData(chartsConfig.jsonList,chartsConfig.jsonConfig);
	    }
		// 初始化配置
	    this.initChartsConfig(chartsConfig);
		// 数据加载
		this.initSeries(chartsConfig.chartsData);
        // X轴和Y轴
        this.initXyAxis();
	    // 创建图表
	    this.initRequire(chartsConfig);
	},
	initChartsConfig: function(chartsConfig) {
		
	},
	initRequire: function(chartsConfig) {
		// X轴
		var xAxis = this.xAxis;
		// Y轴
        var yAxis = this.yAxis;
        // 图例
		var legend = this.legend;
		// 通用数据
        var series = this.series;
        // 提示框
        var tooltip = this.tooltip;
        // 工具箱
        var toolbox = this.toolbox;
        // 渲染容器
        var render = chartsConfig.render;
        // 雷达图设置
        var radar = this.radar;
        // 视觉映射组件
        var visualMap = this.visualMap;
        // 主题
        var theme = chartsConfig.theme;
		
        var myChart = echarts.init(document.getElementById(render),theme);
        var option = {
            tooltip: tooltip,
            legend: legend,
            toolbox: toolbox,
            xAxis: xAxis,
            yAxis: yAxis,
            series: series,
            radar: radar,
            visualMap: visualMap
        };
        
        // echarts外部配置
        var optionConfig = chartsConfig.option;
        if(optionConfig) {
        	for(var i in optionConfig) {
        		var item = eval("option." + i);
        		if(!item) { // option -> grid
        			eval("option." + i + "=optionConfig." + i);
        		} else { // option -> grid -> x,y,y2
        			var itemx = eval("optionConfig." + i);
        			if(itemx) {
        				for(var j in itemx) {
        					eval("option." + i + "." + j + "=itemx." + j);
        				}
        			}
        		}
        	}
        }
        
        myChart.setOption(option);
	},
	initXyAxis: function() {
		
	},
	initSeries: function(chartsData) {
        this.xAxis.data = chartsData.x;
        this.series = chartsData.y;
        this.legend = {};
        this.legend.data = [];

        for(var i=0;i<this.series.length;i++) {
        	// 图表类型
        	this.series[i].type = this.chartstype;
        	// 添加legend
        	if(this.series[i].name != undefined) {
        		this.legend.data.push(this.series[i].name);
        	}
        	// 图表数据样式
        	if(this.itemStyles) {
        		if(this.itemStyles.length > 1 && this.itemStyles[i]) {
        			this.series[i].itemStyle = this.itemStyles[i];
        		}
        		if(this.itemStyles.length == 1 && this.itemStyles[0]) {
        			this.series[i].itemStyle = this.itemStyles[0];
        		}
        		this.series[i].symbol = 'none';
        	}
        }
        this.setSeries();
	},
	setSeries: function() {
		
	},
	/**
	 * chartsData = {
	 *     x:['2016-01-01','2016-01-02','2016-01-03','2016-01-04','2016-01-05','2016-01-06','2016-01-07'],
	 *     y:[{
	 *         name: '页面浏览量',data: [11, 11, 15, 13, 12, 13, 10],index: 'pv'
	 *     },{
	 *         name: '访问者数',data: [1, 4, 2, 5, 3, 2, 1],index: 'uv',
	 *         markPoint: {
	 *             data: [
	 *                 {type: 'max', name: '最大值'},
	 *                 {type: 'min', name: '最小值'}
	 *             ]
	 *         }
	 *     }]
	 * };
	 */
	getchartsData: function(jsonList,jsonConfig) {
		if(jsonConfig == undefined || jsonConfig.x == undefined || jsonConfig.y == undefined || jsonConfig.y.length < 1) {
			return ;
		}
		
		var chartsData = {};
		var x = [];
		var y = [];
		
		// 定义x
		var configx = jsonConfig.x;
		// 定义y
		var configy = jsonConfig.y;
		// 定义c
		var configc = jsonConfig.c;
		// 填充y中非数据部分
		for(var i=0;i<configy.length;i++) {
		    var item = configy[i];
		    var yobject = {};
		    // pv
		    yobject.index = item;
		    // [11, 11, 15, 13, 12, 13, 10]
		    yobject.data = [];
		    // 页面浏览量
		    yobject.name = jsonConfig.f[i];
		    
		    if(configc) {
			    // 自定义图表参数
			    for(var j=0;j<configc.length;j++) {
			    	var c = configc[j];
			    	if(c.index == item) {
			    		// yobject.markPoint = c.markPoint;
			    		for(var k in c) {
			    			if(k != 'index') {
			    				eval("yobject." + k + "= c." + k);
			    			}
			    		}
			    	}
			    }
		    }
		    
		    y.push(yobject);
		}
		// 填充数据
		for(var i=0;i<jsonList.length;i++) {
		    var item = jsonList[i];
		    // 填充x
		    x.push(eval("item." + configx));
		    // 填充y
		    for(var j=0;j<y.length;j++) {
		    	var index = y[j].index;
		    	y[j].data.push(eval("item." + index));
		    }
		}
		
		chartsData.x = x;
		chartsData.y = y;
		
		return chartsData;
	}
}
/**
 * 工具类
 */
TerrenCharts.echarts.util = {
	createRandomItemStyle: function() {
		return {
            normal: {
                color: 'rgb(' + [
                    Math.round(Math.random() * 160),
                    Math.round(Math.random() * 160),
                    Math.round(Math.random() * 160)
                ].join(',') + ')'
            }
        };
	},
	setConfig: function(basic,c) {
		for(var i in c) {
			if(typeof(c[i]) == "object") {
				if(!basic[i]) {
					basic[i] = {};
				}
				TerrenCharts.echarts.util.setConfig(basic[i],c[i]);
			} else {
				basic[i] = c[i];
			}
		}
	}
}
/**
 * 线形图
 * @param chartsConfig
 */
TerrenCharts.echarts.lineCharts = function(chartsConfig) {
	this.chartstype = "line";
    this.initCharts(chartsConfig);
}
TerrenCharts.echarts.lineCharts.prototype = new TerrenCharts.echarts();
/**
 * 柱状图
 * @param chartsConfig
 */
TerrenCharts.echarts.columnCharts = function(chartsConfig) {
    this.chartstype = 'bar';
    this.tooltip = {
        trigger: "axis",
        axisPointer: {
        	type: 'shadow'
        }
    }
    this.xAxis = {
        type: 'category',
        boundaryGap: true // 边界间隙
    }
    this.initCharts(chartsConfig);
}
TerrenCharts.echarts.columnCharts.prototype = new TerrenCharts.echarts();
TerrenCharts.echarts.columnCharts.prototype.initChartsConfig = function(chartsConfig) {
	if(chartsConfig && chartsConfig.ctype && chartsConfig.ctype == 'bar') {
		TerrenCharts.echarts.columnCharts.prototype.initXyAxis = this.initBarXyAxis;
		TerrenCharts.echarts.columnCharts.prototype.setSeries = this.setBarSeries;
	} else {
		TerrenCharts.echarts.columnCharts.prototype.initXyAxis = function() {};
		TerrenCharts.echarts.columnCharts.prototype.setSeries = function() {};
	}
}
/**
 * X轴数据倒序
 * X轴和Y轴调换
 */
TerrenCharts.echarts.columnCharts.prototype.initBarXyAxis = function() {
	// X轴数据倒序
	var rdata = [];
	for(var i=this.xAxis.data.length-1;i>=0;i--) {
		rdata.push(this.xAxis.data[i]);
	}
	
	this.xAxis.data = rdata;
	var tAxis = this.xAxis;
	this.xAxis = this.yAxis;
	this.yAxis = tAxis;
}
/**
 * Y轴数据倒序
 */
TerrenCharts.echarts.columnCharts.prototype.setBarSeries = function() {
	// Y轴数据倒序
	for(var i=0;i<this.series.length;i++) {
		var sdata = this.series[i].data;
		if(sdata && sdata.length > 1) {
			var rdata = [];
			for(var j=sdata.length-1;j>=0;j--) {
				rdata.push(sdata[j]);
			}
			this.series[i].data = rdata;
		}
	}
}
/**
 * 个性化线形图
 * @param chartsConfig
 */
TerrenCharts.echarts.lineStyleCharts = function(chartsConfig) {
	this.symbols = ['emptyTriangle','circle','emptyDiamond','triangle','emptyCircle','diamond'];
	this.symbolSize = 10;
	this.markPoint = {
        data: [
	        {type: 'max', name: '最大值'},
	        {type: 'min', name: '最小值'}
	    ]
	}
	this.itemStyles = [{
        normal: {
        	color: '#d87a80',
            lineStyle: {
                width: 2,
                type: 'dashed'
            }
        }
    },{
    	normal: {
    		color: '#ffb980'
        }
    },{
    	normal: {
    		color: '#008acd'
        }
    },{
    	normal: {
    		color: '#b6a2de'
        }
    },{
    	normal: {
    		color: '#2ec7c9'
        }
    }]
    this.initCharts(chartsConfig);
}
TerrenCharts.echarts.lineStyleCharts.prototype = new TerrenCharts.echarts.lineCharts();
TerrenCharts.echarts.lineStyleCharts.prototype.setSeries = function() {
	for(var i=0;i<this.series.length;i++) {
		// 图标样式
		if(this.symbols) {
	        var symbol = '';
			if(i < this.symbols.length) {
		        symbol = this.symbols[i];
			}
			// 平滑线
			if(symbol != 'emptyTriangle') {
			    this.series[i].smooth = true;
			}
			this.series[i].symbol = symbol;
		}
		// 图标尺寸
		if(this.symbolSize) {
	        this.series[i].symbolSize = this.symbolSize;
		}
		// 最大值，最小值
		if(this.markPoint && !this.series[i].markPoint) {
			this.series[i].markPoint = this.markPoint;
		}
    }
}
/**
 * 个性化柱状图
 * @param chartsConfig
 */
TerrenCharts.echarts.columnStyleCharts = function(chartsConfig) {
	var position = "top";
	if(chartsConfig && chartsConfig.ctype && chartsConfig.ctype == 'bar') {
		position = "right";
	}
	this.itemStyles = [{
        normal: {
            color: function(params) {
                var colorList = [
                    '#C1232B','#B5C334','#FCCE10','#E87C25','#27727B',
                    '#FE8463','#9BCA63','#FAD860','#F3A43B','#60C0DD',
                    '#D7504B','#C6E579','#F4E001','#F0805A','#26C0C0'
                ];
                if(params.dataIndex < 0) {
            		return colorList[0];
            	}
                var index = params.dataIndex % colorList.length;
                // 颜色倒置
                if(chartsConfig && chartsConfig.ctype && chartsConfig.ctype == 'bar') {
                	var dataSize = chartsConfig.jsonList.length;
                	if(dataSize > colorList.length) {
                		dataSize = colorList.length;
                	}
                	index = dataSize - index - 1;
                }
                return colorList[index];
            },
            label: {
                show: true,
                position: position,
                formatter: '{c}'
            }
        }
    }]
    this.initCharts(chartsConfig);
}
TerrenCharts.echarts.columnStyleCharts.prototype = new TerrenCharts.echarts.columnCharts();
/**
 * 饼状图
 * @param chartsConfig
 */
TerrenCharts.echarts.pieCharts = function(chartsConfig) {
	this.chartstype = "pie";
	this.tooltip = {
	    trigger: "item"
	}
	this.legend = {
		x: 'left',
		orient: 'vertical'
	}
    this.initCharts(chartsConfig);
}
TerrenCharts.echarts.pieCharts.prototype = new TerrenCharts.echarts();
TerrenCharts.echarts.pieCharts.prototype.initXyAxis = function() {
	this.xAxis = undefined;
    this.yAxis = undefined;
}
/**
 * chartsData = {
 *     y:[{
 *         name: '访问来源',
 *         data: [
 *             {value:599, name:'直接访问'},
 *             {value:290, name:'搜索引擎'}
 *         ]
 *     }]
 * };
 */
TerrenCharts.echarts.pieCharts.prototype.getchartsData = function(jsonList,jsonConfig) {
	if(jsonConfig == undefined || jsonConfig.x == undefined || jsonConfig.y == undefined || jsonConfig.y.length < 1) {
		return ;
	}
	
	var chartsData = {};
	var y = [];
	
	var yobject = {};
	// 访问来源
	yobject.name = jsonConfig.n;
	yobject.data = [];
	// name
	var name = jsonConfig.x;
	// value
	var value = jsonConfig.y[0];
	
	for(var i=0;i<jsonList.length;i++) {
		var item = jsonList[i];
		// {value:599, name:'直接访问'},
		var obj = {value:item[value], name:item[name]};
		yobject.data.push(obj);
	}
	y.push(yobject);
	chartsData.y = y;
	
	return chartsData;
}
TerrenCharts.echarts.pieCharts.prototype.initSeries = function(chartsData) {
    this.series = chartsData.y;
    this.legend.data = [];
    // 图表类型
    this.series[0].type = this.chartstype;
 	var sData = this.series[0].data;
 	for(var j=0;j<sData.length;j++) {
 		// 添加legend
 		this.legend.data.push(sData[j].name);
 	}
}
/**
 * 雷达图
 * @param chartsConfig
 */
TerrenCharts.echarts.radarCharts = function(chartsConfig) {
	this.chartstype = "radar";
	this.tooltip = {
	    trigger: "item",
	    backgroundColor : 'rgba(0,0,250,0.2)'
	}
	this.legend = {
	}
	this.itemStyles = [{
		normal: {
			lineStyle: {
				width:1
			}
	    },
	    emphasis: {
	    	areaStyle: {color:'rgba(0,250,0,0.3)'}
	    }
    }]
    this.initCharts(chartsConfig);
}
TerrenCharts.echarts.radarCharts.prototype = new TerrenCharts.echarts.pieCharts();
/**
 * chartsData = {
 *     x:['2015','2014','2013','2012','2011'],
 *     y:[{
 *         name: '浏览器类型',data: [{value: 599,name: 2015},{value: 398,name: 2014}...]
 *     },{
 *         name: '浏览器类型',data: [{value: 589,name: 2014},{value: 388,name: 2014}...
 *     }],
 *     p: {
 *         indicator: [
 *             {text: 'Chrome',max: 599},
 *             {text: 'Firefox',max: 599},
 *             ...
 *         ]
 *     }
 * };
 */
TerrenCharts.echarts.radarCharts.prototype.getchartsData = function(jsonList,jsonConfig) {
	if(jsonConfig == undefined || jsonConfig.x == undefined || jsonConfig.y == undefined || jsonConfig.y.length < 1) {
		return ;
	}
	
	var chartsData = {};
	var x = [];
	var y = [];
	var p = {
		indicator: [
		]
	};
	var max = 0;
	
	// 定义x
	var configx = jsonConfig.x;
	// 定义y
	var configy = jsonConfig.y;
	// 定义f
	var configf = jsonConfig.f;
	
	// 填充数据
	for(var i=0;i<jsonList.length;i++) {
	    var item = jsonList[i];
	    // 填充x
	    x.push(eval("item." + configx));
	    // 填充y
	    var yobject = {};
	    yobject.data = [];
	    yobject.name = jsonConfig.n;
	    var dataValue = [];
	    for(var j=0;j<configy.length;j++) {
	    	var value = eval("item." + configy[j]);
	    	if(max < value) {
	    		max = value;
	    	}
	    	dataValue.push(value);
	    }
	    yobject.data.push({value: dataValue,name: x[i]});
	    y.push(yobject);
	}
	
	for(var i=0;i<configf.length;i++) {
		p.indicator.push({text: configf[i],max: max});
	}
	
	chartsData.x = x;
	chartsData.y = y;
	chartsData.p = p;
	
	return chartsData;
}
TerrenCharts.echarts.radarCharts.prototype.initSeries = function(chartsData) {
    this.series = chartsData.y;
    this.legend = {};
    this.legend.data = chartsData.x;
    this.radar = chartsData.p;

    for(var i=0;i<this.series.length;i++) {
    	// 图表类型
    	this.series[i].type = this.chartstype;
    	// 图表数据样式
    	if(this.itemStyles) {
    		if(this.itemStyles.length > 1 && this.itemStyles[i]) {
    			this.series[i].itemStyle = this.itemStyles[i];
    		}
    		if(this.itemStyles.length == 1 && this.itemStyles[0]) {
    			this.series[i].itemStyle = this.itemStyles[0];
    		}
    		this.series[i].symbol = 'none';
    	}
    }
    
}
/**
 * 中国地图/世界地图
 * @param chartsConfig
 */
TerrenCharts.echarts.mapCharts = function(chartsConfig) {
	this.chartstype = "map";
	this.mapType = chartsConfig.mapType;
	this.toolbox = {
		show: true,
        orient: 'vertical',
        left: 'right',
        top: 'center',
        feature: {
        	dataView: {readOnly: false},
            restore: {},
            saveAsImage: {}
        }
    }
	this.visualMap = {
        min: 0,
        left: 'left',
        top: 'bottom',
        text: ['高','低'],
        calculable: true
    }
	this.nameMap = {
        'Afghanistan':'阿富汗',
        'Angola':'安哥拉',
        'Albania':'阿尔巴尼亚',
        'United Arab Emirates':'阿联酋',
        'Argentina':'阿根廷',
        'Armenia':'亚美尼亚',
        'French Southern and Antarctic Lands':'法属南半球和南极领地',
        'Australia':'澳大利亚',
        'Austria':'奥地利',
        'Azerbaijan':'阿塞拜疆',
        'Burundi':'布隆迪',
        'Belgium':'比利时',
        'Benin':'贝宁',
        'Burkina Faso':'布基纳法索',
        'Bangladesh':'孟加拉国',
        'Bulgaria':'保加利亚',
        'The Bahamas':'巴哈马',
        'Bosnia and Herzegovina':'波斯尼亚和黑塞哥维那',
        'Belarus':'白俄罗斯',
        'Belize':'伯利兹',
        'Bermuda':'百慕大',
        'Bolivia':'玻利维亚',
        'Brazil':'巴西',
        'Brunei':'文莱',
        'Bhutan':'不丹',
        'Botswana':'博茨瓦纳',
        'Central African Republic':'中非共和国',
        'Canada':'加拿大',
        'Switzerland':'瑞士',
        'Chile':'智利',
        'China':'中国',
        'Ivory Coast':'象牙海岸',
        'Cameroon':'喀麦隆',
        'Democratic Republic of the Congo':'刚果民主共和国',
        'Republic of the Congo':'刚果共和国',
        'Colombia':'哥伦比亚',
        'Costa Rica':'哥斯达黎加',
        'Cuba':'古巴',
        'Northern Cyprus':'北塞浦路斯',
        'Cyprus':'塞浦路斯',
        'Czech Republic':'捷克共和国',
        'Germany':'德国',
        'Djibouti':'吉布提',
        'Denmark':'丹麦',
        'Dominican Republic':'多明尼加共和国',
        'Algeria':'阿尔及利亚',
        'Ecuador':'厄瓜多尔',
        'Egypt':'埃及',
        'Eritrea':'厄立特里亚',
        'Spain':'西班牙',
        'Estonia':'爱沙尼亚',
        'Ethiopia':'埃塞俄比亚',
        'Finland':'芬兰',
        'Fiji':'斐',
        'Falkland Islands':'福克兰群岛',
        'France':'法国',
        'Gabon':'加蓬',
        'United Kingdom':'英国',
        'Georgia':'格鲁吉亚',
        'Ghana':'加纳',
        'Guinea':'几内亚',
        'Gambia':'冈比亚',
        'Guinea Bissau':'几内亚比绍',
        'Equatorial Guinea':'赤道几内亚',
        'Greece':'希腊',
        'Greenland':'格陵兰',
        'Guatemala':'危地马拉',
        'French Guiana':'法属圭亚那',
        'Guyana':'圭亚那',
        'Honduras':'洪都拉斯',
        'Croatia':'克罗地亚',
        'Haiti':'海地',
        'Hungary':'匈牙利',
        'Indonesia':'印尼',
        'India':'印度',
        'Ireland':'爱尔兰',
        'Iran':'伊朗',
        'Iraq':'伊拉克',
        'Iceland':'冰岛',
        'Israel':'以色列',
        'Italy':'意大利',
        'Jamaica':'牙买加',
        'Jordan':'约旦',
        'Japan':'日本',
        'Kazakhstan':'哈萨克斯坦',
        'Kenya':'肯尼亚',
        'Kyrgyzstan':'吉尔吉斯斯坦',
        'Cambodia':'柬埔寨',
        'South Korea':'韩国',
        'Kosovo':'科索沃',
        'Kuwait':'科威特',
        'Laos':'老挝',
        'Lebanon':'黎巴嫩',
        'Liberia':'利比里亚',
        'Libya':'利比亚',
        'Sri Lanka':'斯里兰卡',
        'Lesotho':'莱索托',
        'Lithuania':'立陶宛',
        'Luxembourg':'卢森堡',
        'Latvia':'拉脱维亚',
        'Morocco':'摩洛哥',
        'Moldova':'摩尔多瓦',
        'Madagascar':'马达加斯加',
        'Mexico':'墨西哥',
        'Macedonia':'马其顿',
        'Mali':'马里',
        'Myanmar':'缅甸',
        'Montenegro':'黑山',
        'Mongolia':'蒙古',
        'Mozambique':'莫桑比克',
        'Mauritania':'毛里塔尼亚',
        'Malawi':'马拉维',
        'Malaysia':'马来西亚',
        'Namibia':'纳米比亚',
        'New Caledonia':'新喀里多尼亚',
        'Niger':'尼日尔',
        'Nigeria':'尼日利亚',
        'Nicaragua':'尼加拉瓜',
        'Netherlands':'荷兰',
        'Norway':'挪威',
        'Nepal':'尼泊尔',
        'New Zealand':'新西兰',
        'Oman':'阿曼',
        'Pakistan':'巴基斯坦',
        'Panama':'巴拿马',
        'Peru':'秘鲁',
        'Philippines':'菲律宾',
        'Papua New Guinea':'巴布亚新几内亚',
        'Poland':'波兰',
        'Puerto Rico':'波多黎各',
        'North Korea':'北朝鲜',
        'Portugal':'葡萄牙',
        'Paraguay':'巴拉圭',
        'Qatar':'卡塔尔',
        'Romania':'罗马尼亚',
        'Russia':'俄罗斯',
        'Rwanda':'卢旺达',
        'Western Sahara':'西撒哈拉',
        'Saudi Arabia':'沙特阿拉伯',
        'Sudan':'苏丹',
        'South Sudan':'南苏丹',
        'Senegal':'塞内加尔',
        'Solomon Islands':'所罗门群岛',
        'Sierra Leone':'塞拉利昂',
        'El Salvador':'萨尔瓦多',
        'Somaliland':'索马里兰',
        'Somalia':'索马里',
        'Republic of Serbia':'塞尔维亚共和国',
        'Suriname':'苏里南',
        'Slovakia':'斯洛伐克',
        'Slovenia':'斯洛文尼亚',
        'Sweden':'瑞典',
        'Swaziland':'斯威士兰',
        'Syria':'叙利亚',
        'Chad':'乍得',
        'Togo':'多哥',
        'Thailand':'泰国',
        'Tajikistan':'塔吉克斯坦',
        'Turkmenistan':'土库曼斯坦',
        'East Timor':'东帝汶',
        'Trinidad and Tobago':'特里尼达和多巴哥',
        'Tunisia':'突尼斯',
        'Turkey':'土耳其',
        'United Republic of Tanzania':'坦桑尼亚联合共和国',
        'Uganda':'乌干达',
        'Ukraine':'乌克兰',
        'Uruguay':'乌拉圭',
        'United States of America':'美国',
        'Uzbekistan':'乌兹别克斯坦',
        'Venezuela':'委内瑞拉',
        'Vietnam':'越南',
        'Vanuatu':'瓦努阿图',
        'West Bank':'西岸',
        'Yemen':'也门',
        'South Africa':'南非',
        'Zambia':'赞比亚',
        'Zimbabwe':'津巴布韦'
    }
    this.initCharts(chartsConfig);
}
TerrenCharts.echarts.mapCharts.prototype = new TerrenCharts.echarts.pieCharts();
/**
 * chartsData = {
 *     y:[{
 *         name: '页面浏览量',data: [{name:'北京',value:21},{name:'天津',value:31}],index: 'pv'
 *     },{
 *         name: '访问者数',data: [{name:'北京',value:19},{name:'天津',value:28}],index: 'uv'
 *     }]
 * };
 */
TerrenCharts.echarts.mapCharts.prototype.getchartsData = function(jsonList,jsonConfig) {
	if(jsonConfig == undefined || jsonConfig.x == undefined || jsonConfig.y == undefined || jsonConfig.y.length < 1) {
		return ;
	}
	
	var chartsData = {};
	var y = [];
	
	// 定义x(name)
	var configx = jsonConfig.x;
	// 定义y
	var configy = jsonConfig.y;
	// 定义c
	var configc = jsonConfig.c;
	
	// 填充y中非数据部分
	for(var i=0;i<configy.length;i++) {
	    var item = configy[i];
	    var yobject = {};
	    // pv,uv
	    yobject.index = item;
	    // [{...,value:21},{...,value:31}]
	    yobject.data = [];
	    // 页面浏览量,访问者数
	    yobject.name = jsonConfig.f[i];
	    
	    y.push(yobject);
	}
	var max = 0;
	// 填充数据
	for(var i=0;i<jsonList.length;i++) {
	    var item = jsonList[i];
	    // 填充y
	    for(var j=0;j<y.length;j++) {
	    	var index = y[j].index;
	    	var value = eval("item." + index);
	    	if(max < value) {
	    		max = value;
	    	}
	    	var mobject = {name: eval("item." + configx),value: value};
	    	y[j].data.push(mobject);
	    }
	}
	
	chartsData.y = y;
	chartsData.max = max;
	if(jsonConfig.c) {
		chartsData.config = jsonConfig.c;
	}
	
	return chartsData;
}
TerrenCharts.echarts.mapCharts.prototype.initSeries = function(chartsData) {
    this.series = chartsData.y;
    this.visualMap.max = chartsData.max;
    this.legend.data = [];
    var config = chartsData.config;

    for(var i=0;i<this.series.length;i++) {
    	// 加载config设置
    	if(config) {
        	TerrenCharts.echarts.util.setConfig(this.series[i],config);
        }
    	// 图表类型
    	this.series[i].type = this.chartstype;
    	// 地图类型
    	if(this.series[i].mapType == undefined) {
    		this.series[i].mapType = this.mapType;
    	}
    	// 国家名称映射
    	if(this.series[i].mapType == 'world') {
    		this.series[i].nameMap = this.nameMap;
    		this.legend = undefined;
    	}
    	if(this.series[i].mapType == 'china') {
	    	// 添加legend
	    	if(this.series[i].name != undefined) {
	    		this.legend.data.push(this.series[i].name);
	    	}
    	}
    }
}
/**
 * 关系图
 * @param chartsConfig
 */
TerrenCharts.echarts.graphCharts = function(chartsConfig) {
	this.chartstype = "graph";
	this.tooltip = {
        trigger: 'item',
        formatter: function (params, ticket, callback) {
        	if(params.data.id != undefined) {
        		return params.name + " : " + params.data.val;
        	} else {
        		return params.data.sourceName + " > " + params.data.targetName;
        	}
        }
    }
	this.legend = {
        x: 'left'
    }
	this.symbolTypes = ["circle","triangle","diamond","pin","arrow"];
    this.initCharts(chartsConfig);
}
TerrenCharts.echarts.graphCharts.prototype = new TerrenCharts.echarts.pieCharts();
/**
 * chartsData = {
 *     nodes:[
 *         {id:0,name:'人民网',value:10,val:200,category:1},
 *         {id:1,name:'要闻部',value:10,val:100,category:1},
 *         {id:2,name:'时政',value:10,val:80,category:1},
 *         {id:3,name:'国际',value:10,val:20,category:1}
 *     ],
 *     links: [
 *         {source: 0,target: 1},
 *         {source: 1,target: 2},
 *         {source: 1,target: 3}
 *     ],
 *     labels: ['聚类1','聚类2','聚类3']
 * };
 */
TerrenCharts.echarts.graphCharts.prototype.getchartsData = function(jsonList,jsonConfig) {
	if(jsonConfig == undefined) {
		return ;
	}
	
	var chartsData = {};
	var nodes = [];
    var links = [];
    var labels = jsonConfig.labels ? jsonConfig.labels : [];
    
    var configName = jsonConfig.name ? jsonConfig.name : "name";
    var configValue = jsonConfig.value ? jsonConfig.value : "value";
    var configChildren = jsonConfig.children ? jsonConfig.children : "children";
    
    var nodeid = 0;
    function pushNode(jsonList, parentNode) {
    	for(var i=0;i<jsonList.length;i++) {
    		var item = jsonList[i];
    		
    		var name = eval("item." + configName);
    		var value = eval("item." + configValue);
    		
    		// 分类信息
    		var category = i;
    		// 层级
    		var level = 0;
    		var sumValue = value;
    		if(parentNode) {
    			if(parentNode.category != undefined) {
    				category = parentNode.category;
    			}
    			if(parentNode.level != undefined) {
    				level = parentNode.level + 1;
    			}
    			if(parentNode.val != undefined) {
    				sumValue = parentNode.val;
    			}
    		}
    		
			var node = {id:nodeid,name:name,val:value,value:size,category:category,draggable:true,
						level:level,sumValue: sumValue};
			nodes.push(node);
			nodeid++;
			
			var isChildren = eval("item." + configChildren) ? 1 : 0;
			if(isChildren) {
				pushNode(item.children, node);
			}
			
			if(node && parentNode) {
				links.push({
			        source : parentNode.id,
			        target : node.id,
			        sourceName : parentNode.name,
			        targetName : node.name,
			    })
			}
    	}
    }
    
	pushNode(jsonList);
	
	for(var i=0;i<nodes.length;i++) {
		var node = nodes[i];
		
		// 按比例显示大小
		var n = node.val / node.sumValue;
		var size = jsonConfig.size == undefined ? 20 : jsonConfig.size;
		if(n > 1) {
			n = 1;
		}
		if(jsonConfig.minSize != undefined) {
			if(n < jsonConfig.minSize) {
				n = jsonConfig.minSize;
			}
		}
		var symbolSize = size * n;
		
		// 一级节点显示标题
		if(node.level == 0) {
			node.label = {
	            normal: {
	                show: true
	            }
	        };
		}
		node.symbolSize = symbolSize;
		
		if(jsonConfig.symbolStyle) {
			var index = node.category % this.symbolTypes.length;
			node.symbol = this.symbolTypes[index];
		}
		
		// 添加标签
		if(node.level == 0 && !jsonConfig.labels) {
			labels.push(node.name);
		}
	}
	
	chartsData.nodes = nodes;
	chartsData.links = links;
	chartsData.labels = labels;
	if(jsonConfig.c) {
		chartsData.config = jsonConfig.c;
	}
	return chartsData;
}
TerrenCharts.echarts.graphCharts.prototype.initSeries = function(chartsData) {
	var nodes = chartsData.nodes;
    var links = chartsData.links;
    var labels = chartsData.labels;
    var categories = [];
    for(var i=0;i<labels.length;i++) {
    	var categorie = {name: labels[i]};
    	categories.push(categorie);
    }
    this.legend.data = labels;
    
    this.series = [{
        type: this.chartstype,
        layout: 'force',
        data: nodes,
        links: links,
        categories: categories,
        roam: true,
        label: {
            normal: {
                position: 'right',
                formatter: '{b}'
            }
        },
        force: {
            repulsion: 200,
            gravity: 0.5,
            edgeLength: 50
        },
        lineStyle: {
            normal: {
                curveness: 0.3
            }
        }
    }]
    var config = chartsData.config;
    if(config) {
    	TerrenCharts.echarts.util.setConfig(this.series[0],config);
    }
}