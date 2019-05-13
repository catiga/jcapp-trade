package com.jeancoder.trade.internal.incall.cashier

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.counterperm.RoleCusprice
import com.jeancoder.trade.ready.counterperm.RoleDiscount
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.entity.CashDoLog
import com.jeancoder.trade.ready.entity.CollectSetting
import com.jeancoder.trade.ready.ser.CashierService

def jc_name = JC.internal.param('jc_name');
def jc_pwd = JC.internal.param('jc_pwd');
def jc_counter = JC.internal.param('jc_counter');

CashierService ser = CashierService.INSTANCE;
CashCounter counter = ser.get_counter(jc_counter);
if(counter==null||counter.inuse==1) {
	//使用中，不可登入
	return SimpleAjax.notAvailable('counter_in_using,收银台正在使用中');
}

//执行登录操作
def login_result = JC.internal.call('project', '/incall/login', [jc_name:jc_name,jc_pwd:jc_pwd,jc_pid:counter.pid, jc_ip:'127.0.0.1']);

SimpleAjax ret_obj = JackSonBeanMapper.fromJson(login_result, SimpleAjax);

if(login_result==null||!ret_obj.available) {
	return SimpleAjax.notAvailable('uname_pwd_error,密码错误');
}
def login_session = ret_obj.data[0];		//用户登陆及会话信息
def login_basic_id = login_session['basic_id'];
if(login_basic_id) {
	try {
		login_basic_id = new BigInteger(login_basic_id);
	} catch(any) {
		login_basic_id = new BigInteger(0);
	}
}
def login_token = login_session['token'];
def roles = ret_obj.data[1];

//修改收银台状态
//CashDoLog login_success = ser.counter_log_in(counter.pid, counter, jc_name, login_token);
CashDoLog login_success = ser.counter_log_in_with_uid(counter.pid, counter, jc_name, login_basic_id, login_token);

//读取可打折列表
//def dl_list = [new DL(id:1,discount:8), new DL(id:2,discount:8.5), new DL(id:3,discount:9.5), new DL(id:4,discount:10)];
//def ret_discount = new RoleDiscount(dl:dl_list);

def ret_discount = new RoleDiscount();
def ret_cutprice = new RoleCusprice();
if(roles!=null) {
	def sql = 'select * from CollectSetting where flag!=? and role_id in (';
	def params = []; params.add(-1);
	for(x in roles) {
		sql += '?,';
		params.add(x['id']);
	}
	sql = sql.substring(0, sql.length() - 1) + ')';
	if(params.size()>1) {
		def discount_resultset = JcTemplate.INSTANCE().find(CollectSetting, sql, params.toArray());
		ret_discount.add(discount_resultset);
		ret_cutprice.add(discount_resultset);
	}
}
return SimpleAjax.available('', [login_success, ret_discount, ret_cutprice]);




