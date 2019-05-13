package com.jeancoder.trade.entry.mgr

import com.jeancoder.core.result.Result
import com.jeancoder.trade.ready.TestReady
import com.jeancoder.trade.ready.util.TradeTssUtil

def sql = '你好 中国';

Result r = new Result();
r.setView('mgr/hello');
r.addObject('sql', sql);

def sql_2 = TestReady.sql;
r.addObject('sql2', sql_2);

println 'sql=' + sql;
println 'sql2=' + sql_2;

def all_tss = TradeTssUtil.all();
r.addObject('all_tss', all_tss);

println sql;

return r;