package com.jeancoder.trade.interceptor.project

import com.jeancoder.annotation.urlmapped
import com.jeancoder.app.sdk.JC
import com.jeancoder.core.http.JCRequest
import com.jeancoder.trade.ready.dto.SysProjectInfo
import com.jeancoder.trade.ready.util.GlobalHolder
import com.jeancoder.trade.ready.util.NativeUtil

@urlmapped("/")

JCRequest req = JC.request.get();
GlobalHolder.remove();
String domain = req.getServerName();
SysProjectInfo project = NativeUtil.connect(SysProjectInfo.class, 'project', '/incall/project', ["domain":domain]);
GlobalHolder.setProj(project);
req.setAttribute("current_project", project)

//req.setAttribute('pub_bucket', 'https://cdn.iplaysky.com/static/');
//req.setAttribute('pub_bucket', 'http://static.jcloudapp.chinaren.xyz/static/')
req.setAttribute('pub_bucket', 'https://static.hash.bid/static/')

return true;
