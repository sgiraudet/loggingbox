
var spawn = require('child_process').spawn;
var querystring = require('querystring');
var http = require('http');
var os = require("os");
var nconf = require("nconf");
var common = require('./common');

common.initConf();


var args = process.argv.splice(2);

// print process.argv
args.forEach(function (fileName, index, array) {
  console.log('Tail file : '+fileName);
  
  tailFile(fileName, function(data) {
  	var dataArray = data.toString('utf-8').split('\n');
  	for(var i = 0; i < dataArray.length; i++) {
  		var newLine = dataArray[i];
  		if(newLine.length>0) {
  			sendNewLine(fileName, newLine);
  		}
  	}
	
  });
  
});


	

  
function tailFile(filename, callback) {
     var tail = spawn("tail", ["-f","-n", "0", filename]);
	 
     tail.stdout.on("data", function (data) {
	   callback(data);
     }); 
 }
 
 
 function sendNewLine(fileName, newLine) {
 	console.log(newLine);
 	var post_data = querystring.stringify( 
 		{ 
 		 "type" : "default", 
 		 "level" : "INFO", 
 		 "message" : newLine,
		 "file" :  fileName,
 		 "server" : os.hostname(),
		 "date" : new Date().getTime() 
	 	});
		 
 	var options = {
 	  host: 'localhost',
 	  port: nconf.get('http:port'),
 	  path: '/api/log/push',
   	  method: 'POST',
   	  headers: {"content-type": "application/x-www-form-urlencoded", 
             "content-length": post_data.length}
 	};
	
	

 	var request = http.request(options, function(resp){
 	  resp.on('data', function(chunk){
	  
 	  });
 	}).on("error", function(e){
 	  console.log("Got error: " + e.message);
 	});
     request.write(post_data);
     request.end();
 }
 