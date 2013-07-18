from decorator import decorator
from django.http import Http404
from django.template import RequestContext
from pymongo import MongoClient
import time

    
    
    
@decorator
def apiFilter(f, request, *args, **kwargs):
    host = request.META.get('HTTP_HOST', '')
    host_s = host.replace('www.', '').split('.')
    appId = 'none'   
    if len(host_s) >= 2:
        appId = host_s[0] 
    
    if appId != 'none':
        request.appId = appId;
        return f(request, *args, **kwargs)
    else:
        raise Http404

def api_processor(request):
    client = MongoClient()
    db = client.monchais
    return  {'app':db.apps.find_one({ 'id' : request.appId })}



def getApiContext(request, params) : 
    return RequestContext (request,
                      params,
                      [api_processor]
                      )
    
    
    
def convertMongoLogsToJsonLogs(mongoLogs):
    jsonLogs = []
    for mongoLog in mongoLogs :
        jsonLogs.append(convertMongoLogToJsonLog(mongoLog))
    return jsonLogs


def convertMongoLogToJsonLog(mongoLog):
    return {
            'id' : str(mongoLog['_id']),
            'level':mongoLog['level'],
            'host':mongoLog['host'],
            'dataType':mongoLog['dataType'],
            'data':mongoLog['data'],
            'appId':mongoLog['appId'],
            'time': time.mktime(mongoLog['time'].timetuple())*1e3+ mongoLog['time'].microsecond/1e3,
            }
    
