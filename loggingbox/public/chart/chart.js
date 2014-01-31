
var chart = {
	def : null,
	data : new Array(),
	graph : null
}
graph = new Array();



function onChartreceived(chartDefinition) {
	chart.def = chartDefinition;

	var kpiChartDiv = document.createElement('div');
	$(kpiChartDiv).html( '<div>'+chart.def.name+'</div><div class="chart_container">'+
			'<div class="y_axis" id="div'+chart.def._id+'Chart_y_axis"></div>'+
			'<div class="chart"  id="div'+chart.def._id+'Chart"></div></div>');

	$('#chart').append(kpiChartDiv);
	
	getLogs(chart.def.filter, function(logs) {
		chart.data = new Array();
		for(var i = 0; i < logs.length; i++) {
			onLogReceived(logs[i]);
		}
	});
	
	registerLogNewEvent(chart.def.filter, onLogReceived);
}


function onLogReceived(log) {
	var x = null;
	var y = null;
	
	try {
		x = eval('log.'+chart.def.selector.x);
		y =  eval('log.'+chart.def.selector.y);
	}catch (e) {
		console.log('Fail to select chart data in log.'+e);
	}
	
	if(!x || !y) {
		return;
	}
	chart.data.push( {
			x : Math.round(	 x/1000),
			y : y
		});
		
	if(chart.data.length > 300) {
		chart.data.shift();
	}
	if (typeof chart.graph === 'undefined' || chart.graph == null) {
		chart.graph = new Rickshaw.Graph({
			element : document.querySelector('#div'+chart.def._id+'Chart'),
			renderer : 'line',
			width : 580,
			height : 350,
			series : [ {
				color : 'steelblue',
				data : chart.data,
				name : chart.def.name
			} ]
		});
		chart.graph.render();
		
		var hoverDetail = new Rickshaw.Graph.HoverDetail( {
			graph: chart.graph
		} );

		var axes = new Rickshaw.Graph.Axis.Time( {
			graph: chart.graph
		} );
		
	
		var y_axis = new Rickshaw.Graph.Axis.Y( {
		 	        graph: chart.graph,
		 	        orientation: 'left',
		 	        element: document.getElementById('div'+chart.def._id+'Chart_y_axis')
		 	} );
		
		axes.render();
		y_axis.render();
	}else {
		chart.graph.update();
	}
	
}

$(document).ready(function() {
	chartId = $.urlParam('id');
	getChart(chartId, onChartreceived);
});



