from django.conf.urls import patterns, url

from loggingbox.api import views

urlpatterns = patterns('',
    url(r'^logs/list', views.listLogs),
    url(r'^logs/insert', views.insertLog),
)
