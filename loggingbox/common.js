
var nconf = require('nconf');


module.exports = {
  initConf: function () {
	  nconf.argv().env();

	  nconf.file({ file: 'config.json' });
	  
	  //define all default values
	  nconf.defaults({
	      'http': {
	          'port': 80
	      },
		  'mongo' :{
			  'server' : {
				  'host' : 'localhost',
				  'port' : 27017
			  } 
		  }
	  });
  }
};