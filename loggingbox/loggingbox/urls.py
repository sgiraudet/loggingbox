from django.conf.urls import patterns, url, include

from loggingbox import views

urlpatterns = patterns('',
    url(r'^api/', include('loggingbox.api.urls')),
    url(r'^$', views.index)
)
