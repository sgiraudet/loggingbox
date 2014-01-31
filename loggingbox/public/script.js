

var config = {
	formatters : [],
	charts : []
}

registerLogNewEvent(null, insertLog);

getFormatters(function(formatters) {
	config.formatters = formatters;
 		
	getLogs({}, function(data) {
		console.log('data received'+data);
		for(var i = 0; i < data.length; i++) {
	 		insertLog(data[i]);
		}
	});
 });
 

getCharts(function(charts){
	if(charts instanceof Array){
 		config.charts = charts;
	} else if(charts){
		config.charts = new Array(charts);
	}
	refreshCharts();
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
			if($.matchFilter(formatter.filter, log)) {
				return formatter;
			}
		}
	}
	return null;
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


function refreshCharts() {
	$('#live_kpis').html('');
	
	for(var i = 0; i < config.charts.length; i++) {
		var chart = config.charts[i];
		
		// Set attributes as a second parameter
		$('<iframe />', {
		    name: chart.name,
		    id:   chart._id,
		    src : '/chart/?id='+chart._id
		}).appendTo('#live_kpis');
	}
	
}

 // 
// var server = {
// 	
// 	_onmessage : function(m) {
// 
// 		if (m.data) {
// 			var jsonObject = eval('(' + m.data + ')');
// 			if (jsonObject.objectType == 'com.log.model.result.SearchResult') {
// 				search.onSearchResult(jsonObject);
// 			} else if (jsonObject.objectType == 'com.log.model.result.GetLogsResult') {
// 				search.onGetLogsResult(jsonObject);
// 			} 
// 		}
// 	},
// 
// 	_onclose : function(m) {
// 		this._ws = null;
// 	}
// };


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

