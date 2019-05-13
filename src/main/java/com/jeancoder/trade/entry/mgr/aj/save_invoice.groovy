package com.jeancoder.trade.entry.mgr.aj

import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.InvoiceSet
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeInvoice
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.entity.TradePayInfo
import com.jeancoder.trade.ready.ser.InvoiceService
import com.jeancoder.trade.ready.ser.TradeService

def tid = JC.request.param('tid')?.trim();
def pay_name = JC.request.param('pay_name')?.trim();
def pay_tax = JC.request.param('pay_tax')?.trim();
def pay_bank = JC.request.param('pay_bank')?.trim();
def pay_account = JC.request.param('pay_account')?.trim();

def pay_addr = JC.request.param('pay_addr')?.trim();
def pay_phone = JC.request.param('pay_phone')?.trim();

def is_id = JC.request.param('is_id')?.trim();

InvoiceSet invoice_set = InvoiceService.INSTANCE().get_invoice_set(is_id);
if(invoice_set==null) {
	return SimpleAjax.notAvailable('请选择发票设置信息');
}

TradeService t_ser = TradeService.INSTANCE();

TradeInfo trade = t_ser.get_trade(tid);

if(trade==null) return SimpleAjax.notAvailable('trade_not_found');

List<TradePayInfo> trade_pays = t_ser.get_trade_pay(trade);

//获取交易下的订单列表
List<TradeOrder> trade_orders = t_ser.trade_items(trade);
//计算总价
BigDecimal total_amount = new BigDecimal('0');
trade_orders.each({total_amount += it.pay_amount});

TradeInvoice invoice = new TradeInvoice();
invoice.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
invoice.c_time = invoice.a_time;
invoice.flag = 0;
invoice.invo_set_id = invoice_set.id;
invoice.invoice_amount = total_amount;
invoice.is_tax_fee = invoice_set.tax_fee;
invoice.is_unit_name = invoice_set.unit_name;
invoice.is_unit_tax = invoice_set.unit_tax;
invoice.pay_account = pay_account;
invoice.pay_addr = pay_addr;
invoice.pay_bank = pay_bank;
invoice.pay_phone = pay_phone;
invoice.pay_unit_name = pay_name;
invoice.pay_unit_tax = pay_tax;
invoice.t_id = trade.id;
BigInteger invoice_id = JcTemplate.INSTANCE().save(invoice);

return SimpleAjax.available('', invoice_id);

