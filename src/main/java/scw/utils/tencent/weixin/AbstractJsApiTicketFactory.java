package scw.utils.tencent.weixin;

import scw.common.exception.ShuChaoWenRuntimeException;
import scw.utils.tencent.weixin.bean.JsApiTicket;

public abstract class AbstractJsApiTicketFactory implements JsApiTicketFactory {
	private final AccessTokenFactory accessTokenFactory;

	public AbstractJsApiTicketFactory(AccessTokenFactory accessTokenFactory) {
		this.accessTokenFactory = accessTokenFactory;
	}

	public AccessTokenFactory getAccessTokenFactory() {
		return accessTokenFactory;
	}

	public String getAppid() {
		return accessTokenFactory.getAppid();
	}

	public String getAppsecret() {
		return accessTokenFactory.getAppsecret();
	}

	public String getAccessToken() {
		return getAccessTokenFactory().getAccessToken();
	}

	public String getTicket() {
		JsApiTicket jsApiTicket = getJsApiTicketByCache();
		if (jsApiTicket == null || jsApiTicket.isExpires()) {
			jsApiTicket = refreshJsApiTicket();
		}

		if (jsApiTicket == null) {
			throw new ShuChaoWenRuntimeException("无法获取ticket");
		}
		return jsApiTicket.getTicket();
	}

	protected boolean isExpires() {
		JsApiTicket jsApiTicket = getJsApiTicketByCache();
		return jsApiTicket == null || jsApiTicket.isExpires();
	}

	protected abstract JsApiTicket refreshJsApiTicket();

	/**
	 * 从缓存中获取ticket
	 * 
	 * @return
	 */
	protected abstract JsApiTicket getJsApiTicketByCache();
}
