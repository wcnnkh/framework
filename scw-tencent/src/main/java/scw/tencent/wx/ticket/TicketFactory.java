package scw.tencent.wx.ticket;

import scw.beans.annotation.AutoImpl;
import scw.tencent.wx.token.AccessTokenFactory;

@AutoImpl({ MemcachedTicketFactory.class, RedisTicketFactory.class, MemoryTicketFactory.class })
public interface TicketFactory extends AccessTokenFactory {
	String getTicket();
}
