from pymongo import MongoClient

def insertLog(level, host, dataType, data, appId, time):
    client = MongoClient()
    db = client.loggingbox
    log =  {
        "level":level,
        "host":host,
        "dataType":dataType,
        "data":data,
        "appId":appId,
        "time": time,
     }
    db.logs.insert(log)