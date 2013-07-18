from django.http import Http404
from pymongo import MongoClient
from decorator import decorator
from django.http import HttpResponse
from django.template import loader
from django.template import RequestContext
from loggingbox.api import logs
import datetime
import socket
    
    
    
@decorator
def websiteFilter(f, request, *args, **kwargs):
    host = request.META.get('HTTP_HOST', '')
    host_s = host.replace('www.', '').split('.')
    appId = 'none'   
    if len(host_s) >= 2:
        appId = host_s[0] 
    
    if appId != 'none':
        request.appId = appId;
        logs.insertLog('TRACE', socket.gethostname(), 'access', "["+request.META['REMOTE_ADDR']+"] -" + request.path, 'loggingbox', datetime.datetime.utcnow())
        return f(request, *args, **kwargs)
    else:
        raise Http404

def default_processor(request):
    client = MongoClient()
    return  {}


def getDefaultContext(request, params) : 
    return RequestContext (request,
                      params,
                      [default_processor]
                      )



@websiteFilter  
def index(request):
    template = loader.get_template('loggingbox/index.html')
    return HttpResponse(template.render(getDefaultContext(request,{})))
