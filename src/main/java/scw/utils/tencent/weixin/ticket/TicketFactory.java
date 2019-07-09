package scw.utils.tencent.weixin.ticket;

import scw.utils.tencent.weixin.token.AccessTokenFactory;

public interface TicketFactory extends AccessTokenFactory{
	String getTicket();
}
