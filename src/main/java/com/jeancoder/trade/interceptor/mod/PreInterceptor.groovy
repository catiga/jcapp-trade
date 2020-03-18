package com.jeancoder.trade.interceptor.mod

import com.jeancoder.annotation.urlmapped
import com.jeancoder.annotation.urlpassed
import com.jeancoder.app.sdk.JC
import com.jeancoder.core.http.JCRequest
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.trade.ready.dto.AppFunction
import com.jeancoder.trade.ready.dto.SysProjectInfo
import com.jeancoder.trade.ready.util.FuncUtil
import com.jeancoder.trade.ready.util.GlobalHolder
import com.jeancoder.trade.ready.util.NativeUtil


@urlmapped("/")
@urlpassed(['/incall', '/outcall'])

def mod_1 = FuncUtil.build(1, '交易管理', null, 'mgr', 'fa-shopping-cart');
def mod_1_1 = FuncUtil.build(101, '交易查询', 1, 'mgr/index', '', 2);

def mod_2 = FuncUtil.build(2, '系统设置', null, 'sys', 'fa-shopping-cart');
def mod_2_1 = FuncUtil.build(201, '支付方式', 2, 'pt/index', '', 2);
def mod_2_2 = FuncUtil.build(202, '手工折扣设置', 2, 'cashcoll/discount', '', 2);
def mod_2_3 = FuncUtil.build(203, '交易改价设置', 2, 'cashcoll/cusprice', '', 2);
def mod_2_4 = FuncUtil.build(204, '收款单位', 2, 'invset/index', '', 2);

def mod_3 = FuncUtil.build(3, '工作台管理', null, 'cashier', 'fa-money');
def mod_3_1 = FuncUtil.build(301, '收银台设置', 3, 'cashier/index', '', 2);
def mod_3_2 = FuncUtil.build(302, '后厨工作台', 3, 'kitchen/index', '', 2);
 
def result = [];
result.addAll([mod_1, mod_1_1]);
result.addAll([mod_2, mod_2_1, mod_2_2, mod_2_3, mod_2_4]);
result.addAll([mod_3, mod_3_1, mod_3_2]);

/*
try {
	RemoteUtil.connect('project', '/incall/mod/reg', [app_code:'trade', accept:URLEncoder.encode(JackSonBeanMapper.listToJson(result), 'UTF-8')]);
} catch(ANY) {
	println ANY;
	ANY.stackTrace;
}
*/
AppFunction mod_g_main=new AppFunction();
mod_g_main.func_name='交易管理';
mod_1_1.func_info='可查看交易订单的信息';
mod_2_1.func_info='设置设置支付类型';
mod_2_2.func_info='设置折扣的信息';
mod_2_3.func_info='设置交易改价的信息';
mod_2_4.func_info='设置收款单位的详细信息';
mod_3_1.func_info='可查看收银台的日志以及设置收银台的信息';
mod_3_2.func_info='可查看后厨工作台的日志以及设置后厨工作台的信息';
def appFun=[];
appFun.addAll([mod_1, mod_2, mod_3]);
def appFunChild=[];
appFunChild.addAll([mod_1_1, mod_2_1,mod_2_2,mod_2_3,mod_2_4,mod_3_1,mod_3_2]);



JCRequest request = JC.request.get();
request.setAttribute("appMain", mod_g_main);
request.setAttribute('appFun', appFun);
request.setAttribute("appFunChild", appFunChild);
def uri = request.getRequestURI();
def context = request.getContextPath();

def uri_without_code = uri[context.length()+1..-1];
if(uri_without_code.endsWith("/"))
	uri_without_code = uri_without_code[0..-2];
request.setAttribute("__now_uri_", uri_without_code);

List<AppFunction> functions = NativeUtil.connectAsArray(AppFunction, 'project', '/incall/mod/mods', [pid:GlobalHolder.getProj().id,user_id:GlobalHolder.getToken().user.id,app_code:'trade', accept:URLEncoder.encode(JackSonBeanMapper.listToJson(result), 'UTF-8')]);
Map<AppFunction, List<AppFunction>> my_funcs = my_funcs(functions);
request.setAttribute("user_roles_functions", my_funcs);


return true;




def get_by_id(def id, List<AppFunction> functions) {
	for(AppFunction f : functions) {
		if(f.id==id) {
			return f;
		}
	}
	return null;
}



def Map<AppFunction, List<AppFunction>> my_funcs(List<AppFunction> functions) {
	Map<AppFunction, List<AppFunction>> parent_functions = new LinkedHashMap<AppFunction, List<AppFunction>>();
	SysProjectInfo project = GlobalHolder.getProj();
	if(functions!=null&&!functions.isEmpty()) {
		for(AppFunction f : functions) {
			AppFunction parent_f = null;
			List<AppFunction> result_f = new ArrayList<AppFunction>();
			
			//只取两级的判断
			if(f.getLevel().equals(1)) {
				//表示当前这个为一级模块
				parent_f = f;
				for(AppFunction f_2 : functions) {
					if('0000'.equals(f_2.getFunc_type())){
						continue;
					}
					if(f_2.getParent_id()!=null&&f_2.getParent_id().equals(parent_f.getId())) {
						if(f_2.getLimpro().equals("0")&&!project.root) {
							continue;
						}
						result_f.add(f_2);
					}
				}
			} else if(f.getLevel().equals(2)) {
				//表示当前这个为二级模块
				parent_f = get_by_id(f.getParent_id(), functions);
				if(parent_f==null) {
					continue;
				}
				for(AppFunction f_2 : functions) {
					if('0000'.equals(f_2.getFunc_type())){
						continue;
					}
					if(f_2.getParent_id()!=null&&f_2.getParent_id().equals(parent_f.getId())) {
						if(f_2.getLimpro().equals("0")&&!project.root) {
							continue;
						}
						result_f.add(f_2);
					}
				}
			}
			parent_functions.put(parent_f, result_f);
		}
	}
	return parent_functions;
}