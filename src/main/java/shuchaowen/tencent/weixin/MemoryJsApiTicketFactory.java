package shuchaowen.tencent.weixin;

import shuchaowen.tencent.weixin.bean.JsApiTicket;
import shuchaowen.tencent.weixin.process.GetJsApiTicket;

public final class MemoryJsApiTicketFactory extends AbstractJsApiTicketFactory{
	private volatile JsApiTicket jsApiTicket;
	private volatile Object lock = new Object();
	
	public MemoryJsApiTicketFactory(String appid, String appsecret){
		this(new MemoryAccessTokenFactory(appid, appsecret));
	}
	
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
					GetJsApiTicket getJsApiTicket = new GetJsApiTicket(getAccessToken());
					if(getJsApiTicket.isSuccess()){
						jsApiTicket = getJsApiTicket.getTicket();
					}
				}
			}
		}
		return jsApiTicket;
	}
}
