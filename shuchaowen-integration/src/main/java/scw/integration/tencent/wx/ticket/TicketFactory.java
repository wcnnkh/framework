package scw.integration.tencent.wx.ticket;

import scw.beans.annotation.AutoImpl;
import scw.integration.tencent.wx.token.AccessTokenFactory;

@AutoImpl({ MemcachedTicketFactory.class, RedisTicketFactory.class, MemoryTicketFactory.class })
public interface TicketFactory extends AccessTokenFactory {
	String getTicket();
}
