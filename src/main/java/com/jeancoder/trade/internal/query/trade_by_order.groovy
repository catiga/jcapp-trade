package com.jeancoder.trade.internal.query

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.TradeInfo

def order_no = JC.internal.param('order_no');
def order_oc = JC.internal.param('order_oc');
def order_id = JC.internal.param('order_id');
def pid = JC.internal.param('pid');

def sql = 'select * from TradeInfo where id in ( select t_id from TradeOrder where order_id=? and order_num=? and oc=? and flag!=? ) and pid=?';

TradeInfo trade = JcTemplate.INSTANCE().get(TradeInfo, sql, order_id, order_no, order_oc, -1, pid);

return SimpleAjax.available('', trade);
