package shuchaowen.tencent.weixin;

import shuchaowen.tencent.weixin.bean.JsApiTicket;

public final class MemoryJsApiTicketFactory extends AbstractJsApiTicketFactory{
	private volatile JsApiTicket jsApiTicket;
	private volatile Object lock = new Object();
	
	public MemoryJsApiTicketFactory(AccessTokenFactory accessTokenFactory) {
		super(accessTokenFactory);
	}
	
	@Override
	protected JsApiTicket getJsApiTicketByCache() {
		return jsApiTicket;
	}

	@Override
	protected JsApiTicket refreshJsApiTicket() {
		if(isExpires()){
			synchronized (lock) {
				if(isExpires()){
					jsApiTicket = new JsApiTicket(getAccessTokenFactory().getAccessToken());
				}
			}
		}
		return jsApiTicket;
	}
}
