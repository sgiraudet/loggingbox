graphs = [];
graphsData = [];

formatters = [];
var config = {
	formatters : []
}
var socket = io.connect(window.location.origin);
//register to some default events
socket.on('event.log.new', function (log) {
   console.log('New log data'+log);
   insertLog(log);
 });
 
 //start by loading all default data.
 //load formatters
 socket.emit('action.formatter.get', {}, function(formatters) {
 	console.log('Formatters received'+formatters);
	config.formatters = formatters;
 		
	//load all logs
	socket.emit('action.log.get', { 'field': 'data' }, function(data) {
		console.log('data received'+data);
		for(var i = 0; i < data.length; i++) {
	 		insertLog(data[i]);
		}
	});
	
 });





function insertLog(log) {
	var isBottom = $('#logScrollContainer').scrollTop() >= $(
			'#logContainer').height()
			- $('#logScrollContainer').height();

	var logContainer = document.getElementById('logContainer');
	var divData = formatLog(log);
	logContainer.appendChild(divData);
	
	var bufferSize = $('#bufferSizeSelect option:selected').val();  
	if(logContainer.childNodes.length > bufferSize) {
		logContainer.removeChild(logContainer.firstChild);
	} 

	
	if(showLog($(divData))) {
		$(divData).show();
	}else {
		$(divData).hide();
	}
	if (isBottom) {
		$('#logScrollContainer').scrollTop(
				$('#logContainer').height());
	}
	
}





function formatLog(log) {
	var divData = document.createElement('div');
	
	var formatter = selectFormatter(log);
	var htmlData = '['+log._inserted+'] - No formatter for this log';
	if(formatter) {
		htmlData = applyFormatter(formatter, log);
	}
	
	divData.innerHTML = htmlData;
	divData.className = 'log ';
	
	var jsonDiv = document.createElement('div');
	jsonDiv.innerHTML = formatJSON(log, 0);
	$(jsonDiv).hide();
	divData.appendChild(jsonDiv);
	$(divData).click(function(){
		$(jsonDiv).toggle();
	});
	return divData;
}
function formatJSON(jsonObject, level) {

	if(level == 0) {	
		html = '<div class="json-node level'+level+'">';
		html += '<div>{</div>';
		html += formatJSON(jsonObject, 1);
		html += '<div>}</div>';
		html += '</div>';
		return html;
	}
	
	var html = '<div class="json-node level'+level+'">';

	var keys = Object.keys(jsonObject);
	for(var i = 0; i < keys.length; i++) {
		var key = keys[i];
		if(key.indexOf("_") == 0) {
			continue;
		}
		var value = jsonObject[key];
		var delimiter = '';
		if(i < keys.length-1) {
			delimiter = '<span class="delimiter">,<span>'
		}
		if(value == null) {
			html += '<div><span class="key">'+key+'</span>:<span class="value">null</span>'+delimiter+'</div>';
		}else if(typeof value === 'object'){
			html += '<div><span class="key">'+key+'</span>:{</div>';
			html += formatJSON(value, level+1);
			html += '<div>}'+delimiter+'</div>'
		}else if(typeof value === 'string'){
			html += '<div><span class="key">'+key+'</span>:<span class="value">"'+value+'"</span>'+delimiter+'</div>';
		}else {
			html += '<div><span class="key">'+key+'</span>:<span class="value">'+value+'</span>'+delimiter+'</div>';
		}
	}
		
	html += '</div>';
	return html;
	
}

function selectFormatter(log) {
	for(var i = 0; i < config.formatters.length; i++) {
		var formatter = config.formatters[i];
		if(formatter.filter) {
			var keys = Object.keys(formatter.filter);
			for(var j = 0; j < keys.length; j++) {
				key = keys[j];
				value = formatter.filter[key];
				var paths = key.split('.');
				var currentLogPath = log;
				for(var k = 0; k < paths.length; k++) {
					if(!currentLogPath[paths[k]]) {
						return null;
					}
					currentLogPath = currentLogPath[paths[k]];
				}
			}
		}
		return formatter;	
	}
}
function applyFormatter(formatter, log) {
	var html = formatter.output;
	
	while(html.indexOf('{{') >= 0) {
		var start = html.indexOf('{{');
		var end = html.indexOf('}}');
		if(end <= start) {
			return 'Formatter output is not well formatted.';
		}
		var expression = html.substring(start+2, end);
		var selector = $.trim(expression);
		var expressionFormatter = 'default';
		if(expression.indexOf('|')) {
			selector = $.trim(expression.split('|')[0]);
			expressionFormatter = $.trim(expression.split('|')[1]);
		}
		var value = ''; 
		var findValue = true;
		
		var paths = selector.split('.');
		var currentLogPath = log;
		for(var k = 0; k < paths.length; k++) {
			if(!currentLogPath[paths[k]]) {
				findValue = false;
				break;
			}
			currentLogPath = currentLogPath[paths[k]];
		}
		if(findValue) {
			value = currentLogPath;
		}
		
		html = html.substring(0, start)+value+html.substring(end+2); 
	}
	
	
	
	return html;	
}

 
var server = {
	connect : function() {
		
		var location = '';
		if(window.location.protocol == 'https:') {
			location +=  'wss://';
		} else {
			location +=  'ws://';
		}
		location += window.location.hostname;
		if(window.location.port && window.location.port != '80') {
			location += ':'+window.location.port;
		}
		location += '/api/log/get?appId=';
		location += extractUrlParams()['appId'];
		
		this._ws = new WebSocket(location);
		this._ws.onopen = this._onopen;
		this._ws.onmessage = this._onmessage;
		this._ws.onclose = this._onclose;
	},

	_onopen : function() {
		
	},

	_send : function(message) {
		if (this._ws)
			this._ws.send(message);
	},

	send : function(text) {
		if (text != null && text.length > 0)
			server._send(text);
	},

	_onmessage : function(m) {

		if (m.data) {
			var jsonObject = eval('(' + m.data + ')');
			if (jsonObject.objectType == 'com.log.model.KpiDefinition') {
				createKpiChart(jsonObject);
			}else if (jsonObject.objectType == 'com.log.model.Log') {
				var isBottom = $('#logScrollContainer').scrollTop() >= $(
						'#logContainer').height()
						- $('#logScrollContainer').height();

				var logContainer = document.getElementById('logContainer');
				var divData = buildLogDiv(jsonObject);
				logContainer.appendChild(divData);
				
				var bufferSize = $('#bufferSizeSelect option:selected').val();  
				if(logContainer.childNodes.length > bufferSize) {
					logContainer.removeChild(logContainer.firstChild);
				} 

				
				if(showLog($(divData))) {
					$(divData).show();
				}else {
					$(divData).hide();
				}
				if (isBottom) {
					$('#logScrollContainer').scrollTop(
							$('#logContainer').height());
				}
			} else if (jsonObject.objectType == 'com.log.model.Kpi') {
				

				graphsData[jsonObject.kpiDefinitionId].push( {
						x : jsonObject.date,
						y : jsonObject.value
					});
				if(graphsData[jsonObject.kpiDefinitionId].length > 300) {
					graphsData[jsonObject.kpiDefinitionId].shift();
				}
				if (typeof graphs[jsonObject.kpiDefinitionId] === 'undefined') {
					graphs[jsonObject.kpiDefinitionId] = new Rickshaw.Graph({
						element : document.querySelector('#div'+jsonObject.kpiDefinitionId+'Chart'),
						width : 580,
						height : 250,
						series : [ {
							color : 'steelblue',
							data : graphsData[jsonObject.kpiDefinitionId]
						} ]
					});

					var axes = new Rickshaw.Graph.Axis.Time( { graph: graphs[jsonObject.kpiDefinitionId] } );

					var y_axis = new Rickshaw.Graph.Axis.Y( {
					        graph: graphs[jsonObject.kpiDefinitionId],
					        orientation: 'left',
					        tickFormat: Rickshaw.Fixtures.Number.formatKMBT,
					        element: document.getElementById('div'+jsonObject.kpiDefinitionId+'Chart_y_axis'),
					} );
					
					graphs[jsonObject.kpiDefinitionId].render();
				}else {
					graphs[jsonObject.kpiDefinitionId].update();
				}
			} else if (jsonObject.objectType == 'com.log.model.result.SearchResult') {
				search.onSearchResult(jsonObject);
			} else if (jsonObject.objectType == 'com.log.model.result.GetLogsResult') {
				search.onGetLogsResult(jsonObject);
			} 
		}
	},

	_onclose : function(m) {
		this._ws = null;
	}
};

function createKpiChart(kpiDefinition) {
	var kpiChartDiv = document.createElement('div');
	$(kpiChartDiv).html( '<div>'+kpiDefinition.name+'</div><div class="chart_container">'+
			'<div class="y_axis" id="div'+kpiDefinition.id+'Chart_y_axis"></div>'+
			'<div class="chart"  id="div'+kpiDefinition.id+'Chart"></div></div>');
	
	$('#live_kpis').append(kpiChartDiv);
	
	graphsData[kpiDefinition.id] = new Array();
}

function refreshFilter() {
	$('.log').each(function() {
		if(showLog($(this))) {
			$(this).show();
		}else {
			$(this).hide();
		}
	});
}
function updateSearch() {
	var token = $('#searchInput').val();
	search.newToken(token);
	
}


function exportCommand() {
	var token = $('#searchInput').val();
	
	var exportCommand = '{"objectType": "com.log.model.command.Export", "fromDate": "'+new Date(0).getTime()+'", "toDate": "'+new Date().getTime()+'"}';
	server.send(exportCommand); 
}

function showLog(log) {

	var currentFilter = $('#filter').val();
	var levelFilter = $('#levelFilterSelect option:selected').val();  
	
	if (currentFilter) {
		if (log.html().indexOf(currentFilter) < 0) {
			return false;
		} 
	}
	if(levelFilter == 'ALL' ) {
		return true;
	}else if(levelFilter == 'DEBUG' ) {
		return log.attr("class").indexOf('ERROR') > 0 ||
		log.attr("class").indexOf('WARN') > 0 ||
		log.attr("class").indexOf('INFO') > 0 ||
		log.attr("class").indexOf('DEBUG') > 0; 
	}else if(levelFilter == 'INFO' ) {
		return log.attr("class").indexOf('ERROR') > 0 ||
		log.attr("class").indexOf('WARN') > 0 ||
		log.attr("class").indexOf('INFO') > 0; 
	}else if(levelFilter == 'WARN' ) {
		return log.attr("class").indexOf('ERROR') > 0 ||
		log.attr("class").indexOf('WARN') > 0;
	}else if(levelFilter == 'ERROR' ) {
		return log.attr("class").indexOf('ERROR') > 0 ||
		log.attr("class").indexOf('SEVERE') > 0;
	}else if(levelFilter == 'SEVERE' ) {
		return log.attr("class").indexOf('SEVERE') > 0 ;
	}
}

// Register keypress events on the whole document
$(document).keypress(function(e) {
	switch (e.keyCode) {
	case 13:// press enter
		var logContainer = document.getElementById('logContainer');
		var brnode = document.createElement('br');
		logContainer.appendChild(brnode);
		$('#logScrollContainer').scrollTop($('#logContainer').height());
		break;
	}
});

function extractUrlParams(){	
	var t = location.search.substring(1).split('&');
	var f = [];
	for (var i=0; i<t.length; i++){
		var x = t[ i ].split('=');
		f[x[0]]=x[1];
	}
	return f;
}


var search =  {
	searchPageSize : 50,
	
	newToken : function(newToken) {
		this.currentIndex = 0;
		this.resultCount = 0;
		this.token = newToken;
		this.resultLogs = new Array();
		this.resultLogsMap = [];

		var searchCommand = '{"objectType": "com.log.model.command.Search", "size": '+this.searchPageSize+', "from": 0,"token": "'+this.token+'"}';
		server.send(searchCommand); 

		$('#searchCurrentItem').html(0);
		$('#searchItemCount').html(0);
	},
	
	onSearchResult : function(searchResult) {
		this.resultCount = searchResult.itemFounds;
		if(searchResult.logs && searchResult.logs.length > 0) {
			for(var i = 0; i < searchResult.logs.length; i++) {
				var log = searchResult.logs[i];
				if(log.data) {
					this.resultLogs[searchResult.from+i] = log;
					this.resultLogsMap[log.id] = log;
				}
			}
			this.updateCurrentItem(true);
		} else {

			var logContainer = $('#logSearchContainer');
			logContainer.html('No result')
		}
		$('#searchItemCount').html(this.resultCount);
	},
	

	onGetLogsResult : function(getLogsResult) {
		if(!getLogsResult.logs || getLogsResult.logs.length <= 1) {
			if(getLogsResult.ascendingOrder) {
				this.setLoadingBottom(false);
			}else {
				this.setLoadingTop(false);
				this.noDataBefore = true;
			}
			return;
		}
		var logContainer =  $('#logSearchContainer');
		var currentLog = this.resultLogs[this.currentIndex];
		var insertFirst = getLogsResult.logs[0].id > currentLog.id;
		if(insertFirst && getLogsResult.ascendingOrder) {
			this.firstLog = getLogsResult.logs[0];
			if(!this.lastLog) {
				this.lastLog = getLogsResult.logs[getLogsResult.logs.length-1];
			}
			this.setLoadingTop(false);
			var containsCurrentIndex = false;
			for(var i = getLogsResult.logs.length-1; i >= 0; i--) {
				var log = getLogsResult.logs[i];
				var divData;
				//check if the log is the current selected log
				if(log.id == this.resultLogs[this.currentIndex].id) {
					htmlData = log.data;
					var lc = log.data.toLowerCase();
					var indexOfToken = lc.indexOf(this.token.toLowerCase());
					if(indexOfToken > 0) {
						htmlData = htmlData.substring(0, indexOfToken)+'<span class="searchToken">'+
						htmlData.substring(indexOfToken, indexOfToken+this.token.length)+'</span>'+
						htmlData.substring(indexOfToken+this.token.length);
					}
					
					divData = buildLogDiv(log, htmlData);
					containsCurrentIndex = true;
				}else {
					divData = buildLogDiv(log);
				}
				
				//check if the log is in the search result
				if(log.id in this.resultLogsMap) {
					divData.style.fontWeight = 'bold';
				}
				
				logContainer.prepend(divData);
			}
			if(containsCurrentIndex) {
				$('#logSearchScrollContainer').scrollTop(200);
			}
		} else if(insertFirst && !getLogsResult.ascendingOrder) {
			this.setLoadingTop(false);
			var firstChild = $('#logSearchContainer').children("div:first-child");
			this.firstLog = getLogsResult.logs[getLogsResult.logs.length-1];
			for(var i = 1; i < getLogsResult.logs.length; i++) {
				var log = getLogsResult.logs[i];
				var divData;
				divData = buildLogDiv(log);
				//check if the log is in the search result
				if(log.id in this.resultLogsMap) {
					divData.style.fontWeight = 'bold';
				}
				logContainer.prepend(divData);
			}

			$('#logSearchScrollContainer').scrollTop( firstChild.offset().top-logContainer.offset().top);
		} else {
			this.lastLog = getLogsResult.logs[getLogsResult.logs.length-1];
			if(!this.firstLog) {
				this.firstLog = getLogsResult.logs[0].id;
			}
			this.setLoadingBottom(false);
			
			for(var i = 1; i < getLogsResult.logs.length; i++) {
				var log = getLogsResult.logs[i];
				var divData;
				divData = buildLogDiv(log);
				
				//check if the log is in the search result
				if(log.id in this.resultLogsMap) {
					divData.style.fontWeight = 'bold';
				}
				
				logContainer.append(divData);
			}
		}
		

	},
	
	updateCurrentItem : function(ascending) {
		 $('#logSearchContainer').html('');

		 this.setLoadingBottom(false);
		 this.setLoadingTop(false);
		 this.noDataBefore = false;
		
		if(this.currentIndex in this.resultLogs) {
			var log = this.resultLogs[this.currentIndex];
			this.setLoadingTop(true);
			var getLogsCommand = '{"objectType": "com.log.model.command.GetLogs", "offset": 15, "maxItemNumber":100,  "startLogId": "'+log.id+'"}';
			server.send(getLogsCommand);
			$('#searchCurrentItem').html(this.currentIndex+1);
		} else {
			var from;
			if(ascending) {
				from = this.currentIndex;
				
			} else {
				from = this.currentIndex-this.searchPageSize +1;
				if(from < 0) {
					from = 0;
				}
			}
			var searchCommand = '{"objectType": "com.log.model.command.Search", "size": '+this.searchPageSize+', "from": '+from+',"token": "'+this.token+'"}';
			server.send(searchCommand); 
		}
	},
	
	nextItem : function() {
		this.currentIndex++;
		if(this.currentIndex >= this.resultCount) {
			this.currentIndex =  0;
		}
		this.updateCurrentItem(true);
	},
	previousItem : function() {
		this.currentIndex--;
		if(this.currentIndex < 0) {
			this.currentIndex =  this.resultCount-1;
		}
		this.updateCurrentItem(false);
	},
	onScroll : function() {
		if($('#logSearchContainer').height() <= 32) {
			//no enough data to scroll
			return;
		}
		var isBottom = $('#logSearchScrollContainer').scrollTop() >= $(
		'#logSearchContainer').height()- $('#logSearchScrollContainer').height();
		var isTop = $('#logSearchScrollContainer').scrollTop() <= 50;
		if( !this.loadingBottom && isBottom) {
			this.setLoadingBottom(true);
			var getLogsCommand = '{"objectType": "com.log.model.command.GetLogs", "offset": 0, "maxItemNumber":100,  "startLogId": "'+this.lastLog.id+'"}';
			server.send(getLogsCommand);
		}else if(!this.loadingTop && isTop &&  !this.noDataBefore) {

			this.setLoadingTop(true);
			var getLogsCommand = '{"objectType": "com.log.model.command.GetLogs", "ascendingOrder": false,  "offset": 0, "maxItemNumber":100,  "startLogId": "'+this.firstLog.id+'"}';
			server.send(getLogsCommand);
	
		}
	},
	setLoadingTop : function(show) {
		
		if(show && !this.loadingTop){
			this.loadingTop = true;
			
			var logContainer = $('#logSearchContainer');
			var divData = document.createElement('div');
			divData.className = 'loading ';
			logContainer.prepend(divData);
		}else if(!show && this.loadingTop){
			this.loadingTop = false;
			 $('#logSearchContainer').children("div:first-child").remove();
		}
		
	},
	setLoadingBottom : function(show) {
		if(show && !this.loadingBottom){
			this.loadingBottom = true;
			var logContainer = $('#logSearchContainer');
			
			var divData = document.createElement('div');
			divData.className = 'loading ';
			logContainer.append(divData);
			
		}else if(!show && this.loadingBottom){
			 this.loadingBottom = false;
			 $('#logSearchContainer').children("div:last-child").remove();
		}
	}
	
	
};


/*
 * jQuery hashchange event - v1.3 - 7/21/2010
 * http://benalman.com/projects/jquery-hashchange-plugin/
 * 
 * Copyright (c) 2010 "Cowboy" Ben Alman
 * Dual licensed under the MIT and GPL licenses.
 * http://benalman.com/about/license/
 */
(function($,e,b){var c="hashchange",h=document,f,g=$.event.special,i=h.documentMode,d="on"+c in e&&(i===b||i>7);function a(j){j=j||location.href;return"#"+j.replace(/^[^#]*#?(.*)$/,"$1")}$.fn[c]=function(j){return j?this.bind(c,j):this.trigger(c)};$.fn[c].delay=50;g[c]=$.extend(g[c],{setup:function(){if(d){return false}$(f.start)},teardown:function(){if(d){return false}$(f.stop)}});f=(function(){var j={},p,m=a(),k=function(q){return q},l=k,o=k;j.start=function(){p||n()};j.stop=function(){p&&clearTimeout(p);p=b};function n(){var r=a(),q=o(m);if(r!==m){l(m=r,q);$(e).trigger(c)}else{if(q!==m){location.href=location.href.replace(/#.*/,"")+q}}p=setTimeout(n,$.fn[c].delay)}$.browser.msie&&!d&&(function(){var q,r;j.start=function(){if(!q){r=$.fn[c].src;r=r&&r+a();q=$('<iframe tabindex="-1" title="empty"/>').hide().one("load",function(){r||l(a());n()}).attr("src",r||"javascript:0").insertAfter("body")[0].contentWindow;h.onpropertychange=function(){try{if(event.propertyName==="title"){q.document.title=h.title}}catch(s){}}}};j.stop=k;o=function(){return a(q.location.href)};l=function(v,s){var u=q.document,t=$.fn[c].domain;if(v!==s){u.title=h.title;u.open();t&&u.write('<script>document.domain="'+t+'"<\/script>');u.close();q.location.hash=v}}})();return j})()})(jQuery,this);


