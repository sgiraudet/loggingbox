var mongodb = require('mongodb');
var url = require("url");
var querystring = require('querystring');
var express = require('express');
var events = require('events');

var MongoClient = mongodb.MongoClient;
var Server = mongodb.Server;

var app = express();
var server = require('http').createServer(app);
var io =  require('socket.io').listen(server);
var eventEmitter = new events.EventEmitter();


app.configure(function(){
  app.use(express.bodyParser());
});
app.use(express.static(__dirname + '/public')) 
app.post('/api/log/push', pushLog);
server.listen(80);




io.sockets.on('connection', function (socket) {
	console.log('New connection');
	
	eventEmitter.on('event.log.new', function(log) {
		socket.emit('event.log.new', log);
	});
	
	
  	
  	socket.on('my other event', function (data) {
    	console.log("Data received" +data);
  	});
  
  	socket.on('action.log.get', function (args, fn) {
    	console.log("action.log.get");

	  	var mongoClient = new MongoClient(new Server('localhost', 27017));
	  	mongoClient.open(function(err, mongoClient) {
			if(err) {
				 console.log('Error:'+err.stack);
				 return;
			}
		    var db1 = mongoClient.db("test");

		    // Perform a simple insert into a collection
			var collection = db1.collection("log");
			var cursor = collection.find({});
			cursor.toArray(function(err, items) {
				if(err) {
					 console.log('Error:'+err.stack);
					 return;
				}
				fn(items);
	            // Let's close the db
	            db1.close();
			});
		});
    });
  	socket.on('action.formatter.get', function (args, fn) {
    	console.log("action.formatter.get");

	  	var mongoClient = new MongoClient(new Server('localhost', 27017));
	  	mongoClient.open(function(err, mongoClient) {
			if(err) {
				 console.log('Error:'+err.stack);
				 return;
			}
		    var db1 = mongoClient.db("test");

		    // Perform a simple insert into a collection
			var collection = db1.collection("formatter");
			var cursor = collection.find({});
			cursor.toArray(function(err, items) {
				if(err) {
					 console.log('Error:'+err.stack);
					 return;
				}
				fn(items);
	            // Let's close the db
	            db1.close();
			});
		});
    });
	
});




function pushLog(req, res) {
	var log = req.body;
	log['_inserted'] = new Date();
	
	console.log("Push log"+log);
	 
  	var MongoClient = mongodb.MongoClient;
	var Server = mongodb.Server;

  	var mongoClient = new MongoClient(new Server('localhost', 27017));
  	mongoClient.open(function(err, mongoClient) {
		if(err) {
			 console.log('Error:'+err.stack);
			 return;
		}
	    var db1 = mongoClient.db("test");

	    // Perform a simple insert into a collection
		var collection = db1.collection("log");
		collection.insert(log, {}, function(err, item) {
			db1.close();
	    mongoClient.close();
  	  	res.end();
		

		eventEmitter.emit('event.log.new', log);
		
	    });
	 
	});
}



