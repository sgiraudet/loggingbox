graphs = [];
graphsData = [];

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
				
				
				
				var logContainer = $('#logSearchContainer');
				logContainer.html('');
				
				if(jsonObject.logs) {
					for(var i = 0; i < jsonObject.logs.length; i++) {
						var log = jsonObject.logs[i];
						if(log.data) {
							var divData = buildLogDiv(log);
							logContainer.append(divData);
						}
					}
				}
				
				
			} 
		}
	},

	_onclose : function(m) {
		this._ws = null;
	}
};

function buildLogDiv(log) {
	var divData = document.createElement('div');
	var data = log.data;
	if(log.host) {
		data = '['+log.host+']  '+data;
	}
	data = data.replace(/\n/g, "<br />");
	data = data.replace(/\t/g,
			"<span style=\"margin-left:30px;\"></span>");
	divData.innerHTML = data;
	divData.className = 'log ';
	if (log.level) {
		divData.className += log.level;
	}
	return divData;
}

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
	
	var searchCommand = '{"objectType": "com.log.model.command.Search", "token": "'+token+'"}';
	server.send(searchCommand); 
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



/*
 * jQuery hashchange event - v1.3 - 7/21/2010
 * http://benalman.com/projects/jquery-hashchange-plugin/
 * 
 * Copyright (c) 2010 "Cowboy" Ben Alman
 * Dual licensed under the MIT and GPL licenses.
 * http://benalman.com/about/license/
 */
(function($,e,b){var c="hashchange",h=document,f,g=$.event.special,i=h.documentMode,d="on"+c in e&&(i===b||i>7);function a(j){j=j||location.href;return"#"+j.replace(/^[^#]*#?(.*)$/,"$1")}$.fn[c]=function(j){return j?this.bind(c,j):this.trigger(c)};$.fn[c].delay=50;g[c]=$.extend(g[c],{setup:function(){if(d){return false}$(f.start)},teardown:function(){if(d){return false}$(f.stop)}});f=(function(){var j={},p,m=a(),k=function(q){return q},l=k,o=k;j.start=function(){p||n()};j.stop=function(){p&&clearTimeout(p);p=b};function n(){var r=a(),q=o(m);if(r!==m){l(m=r,q);$(e).trigger(c)}else{if(q!==m){location.href=location.href.replace(/#.*/,"")+q}}p=setTimeout(n,$.fn[c].delay)}$.browser.msie&&!d&&(function(){var q,r;j.start=function(){if(!q){r=$.fn[c].src;r=r&&r+a();q=$('<iframe tabindex="-1" title="empty"/>').hide().one("load",function(){r||l(a());n()}).attr("src",r||"javascript:0").insertAfter("body")[0].contentWindow;h.onpropertychange=function(){try{if(event.propertyName==="title"){q.document.title=h.title}}catch(s){}}}};j.stop=k;o=function(){return a(q.location.href)};l=function(v,s){var u=q.document,t=$.fn[c].domain;if(v!==s){u.title=h.title;u.open();t&&u.write('<script>document.domain="'+t+'"<\/script>');u.close();q.location.hash=v}}})();return j})()})(jQuery,this);


