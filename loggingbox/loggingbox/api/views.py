from django.utils import simplejson
from django.http import HttpResponse
from pymongo import MongoClient
from loggingbox.api.util import convertMongoLogsToJsonLogs
from loggingbox.api.util import convertMongoLogToJsonLog
from loggingbox.api.util import apiFilter
from loggingbox.api import logs
import datetime


@apiFilter  
def listLogs(request):
    client = MongoClient()
    db = client.loggingbox
    logs = db.logs.find({'appId' : request.appId})
 
    data = {
            'logs' : convertMongoLogsToJsonLogs(logs)
            }
    return HttpResponse(simplejson.dumps(data), mimetype='application/json')




@apiFilter  
def insertLog(request):
    """Insert a new Log entry. Request must contains data paramter """
  
    time =  datetime.datetime.utcnow()
    level = 'DEBUG'
    host = request.META['REMOTE_ADDR']
    dataType = 'default'
    
    if 'time' in request.POST : 
        time = datetime.datetime.fromtimestamp(request.POST['time'])
    if 'level' in request.POST : 
        level = request.POST['level']
    if 'host' in request.POST : 
        host = request.POST['host']
    if 'dataType' in request.POST : 
        dataType = request.POST['dataType']
      
    log = logs.insertLog(level, host, dataType, request.POST['data'], request.appId, time);
    return HttpResponse(simplejson.dumps(convertMongoLogToJsonLog(log)), mimetype='application/json')


