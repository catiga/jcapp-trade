package com.jeancoder.trade

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.trade.ready.uncertain.KeepClose
import com.jeancoder.trade.ready.util.FuncUtil
import com.jeancoder.trade.ready.util.NativeUtil

JC.interceptor.add("project/PreInterceptor", null);
JC.interceptor.add('token/PreInterceptor', null);
JC.interceptor.add("mod/PreInterceptor", null);

def mod_1 = FuncUtil.build(1, '交易管理', null, 'mgr', 'fa-shopping-cart');
def mod_1_1 = FuncUtil.build(101, '交易查询', 1, 'mgr/index', '', 2);

def mod_2 = FuncUtil.build(2, '系统设置', null, 'sys', 'fa-shopping-cart');
def mod_2_1 = FuncUtil.build(201, '支付方式', 2, 'pt/index', '', 2);
def mod_2_2 = FuncUtil.build(202, '手工折扣设置', 2, 'cashcoll/discount', '', 2);
def mod_2_3 = FuncUtil.build(203, '交易改价设置', 2, 'cashcoll/cusprice', '', 2);
def mod_2_4 = FuncUtil.build(201, '收款单位', 2, 'invset/index', '', 2);

def mod_3 = FuncUtil.build(3, '工作台管理', null, 'cashier', 'fa-money');
def mod_3_1 = FuncUtil.build(301, '收银台设置', 3, 'cashier/index', '', 2);
def mod_3_2 = FuncUtil.build(302, '后厨工作台', 3, 'kitchen/index', '', 2);

def mod_4 = FuncUtil.build(4, '报表工具', null, 'cashier', 'fa-money');
def mod_4_1 = FuncUtil.build(401, '报表管理', 4, 'report/mgr', '', 2);


def result = [];
result.addAll([mod_1, mod_1_1]);
result.addAll([mod_2, mod_2_1, mod_2_2, mod_2_3, mod_2_4]);
result.addAll([mod_3, mod_3_1, mod_3_2]);
result.addAll([mod_4, mod_4_1]);
 
/**
 * 任务代码
 */
JC.thread.timeTask(1*60*1000, 7*50*1000, {
	KeepClose.fuckoff(null);
});
NativeUtil.connect('project', '/incall/mod/mods', [app_code:'trade', accept:URLEncoder.encode(JackSonBeanMapper.listToJson(result), 'UTF-8')]);
 