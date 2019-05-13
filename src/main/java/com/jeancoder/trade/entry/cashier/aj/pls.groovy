package com.jeancoder.trade.entry.cashier.aj

import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.StoreInfo
import com.jeancoder.trade.ready.util.RemoteUtil

List<StoreInfo> stores = RemoteUtil.connectAsArray(StoreInfo.class, 'project', '/incall/mystore', null);

return SimpleAjax.available('', stores);
