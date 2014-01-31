
/*
 * jQuery hashchange event - v1.3 - 7/21/2010
 * http://benalman.com/projects/jquery-hashchange-plugin/
 * 
 * Copyright (c) 2010 "Cowboy" Ben Alman
 * Dual licensed under the MIT and GPL licenses.
 * http://benalman.com/about/license/
 */
(function($,e,b){var c="hashchange",h=document,f,g=$.event.special,i=h.documentMode,d="on"+c in e&&(i===b||i>7);function a(j){j=j||location.href;return"#"+j.replace(/^[^#]*#?(.*)$/,"$1")}$.fn[c]=function(j){return j?this.bind(c,j):this.trigger(c)};$.fn[c].delay=50;g[c]=$.extend(g[c],{setup:function(){if(d){return false}$(f.start)},teardown:function(){if(d){return false}$(f.stop)}});f=(function(){var j={},p,m=a(),k=function(q){return q},l=k,o=k;j.start=function(){p||n()};j.stop=function(){p&&clearTimeout(p);p=b};function n(){var r=a(),q=o(m);if(r!==m){l(m=r,q);$(e).trigger(c)}else{if(q!==m){location.href=location.href.replace(/#.*/,"")+q}}p=setTimeout(n,$.fn[c].delay)}$.browser.msie&&!d&&(function(){var q,r;j.start=function(){if(!q){r=$.fn[c].src;r=r&&r+a();q=$('<iframe tabindex="-1" title="empty"/>').hide().one("load",function(){r||l(a());n()}).attr("src",r||"javascript:0").insertAfter("body")[0].contentWindow;h.onpropertychange=function(){try{if(event.propertyName==="title"){q.document.title=h.title}}catch(s){}}}};j.stop=k;o=function(){return a(q.location.href)};l=function(v,s){var u=q.document,t=$.fn[c].domain;if(v!==s){u.title=h.title;u.open();t&&u.write('<script>document.domain="'+t+'"<\/script>');u.close();q.location.hash=v}}})();return j})()})(jQuery,this);



var socket = io.connect(window.location.origin);


/***** chart functions *****/
function getChart(id, callBack) {
	socket.emit('action.chart.get', {'_id' : id}, function(chart) {
		console.log('Chart received:'+chart);
		callBack(chart);
	});
}

function getCharts(callBack) {
	socket.emit('action.chart.get', {}, function(charts) {
		console.log('Charts received:'+charts);
		callBack(charts);
	});
}


/***** log functions *****/
function getLogs(filter, callBack) {
	socket.emit('action.log.get', filter, function(logs) {
		console.log('Logs received:'+logs);
		callBack(logs);
	});
}

function registerLogNewEvent(filter, callBack) {
	socket.on('event.log.new', function (log) {
		if($.matchFilter(filter, log)) {
		   console.log('New log data'+log);
		   callBack(log);
	   }
	 });
}


/***** log functions *****/
function getFormatters(callback) {
    socket.emit('action.formatter.get', {}, function(formatters) {
    	console.log('Formatters received : '+formatters);
		callback(formatters);
    });
}
 
 


/****** Util functions *****/
 
$.urlParam = function(name){
    var results = new RegExp('[\?\&]' + name + '=([^\&\#]*)').exec(window.location.href);
	if(results && results.length > 0) {
		return results[1];
	}
	return null;
}
$.matchFilter = function(filter, log) {
	if(!filter) {
		return true;
	}
	var keys = Object.keys(filter);
	for(var j = 0; j < keys.length; j++) {
		key = keys[j];
		value = filter[key];
		var paths = key.split('.');
		var currentLogPath = log;
		for(var k = 0; k < paths.length; k++) {
			if(!currentLogPath[paths[k]]) {
				return false;
			}
			currentLogPath = currentLogPath[paths[k]];
		}
		if(currentLogPath != value) {
			return false;
		}
	}
	return true;
}
