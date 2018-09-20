package shuchaowen.web.support.weixin;

import shuchaowen.web.support.weixin.impl.WeiXinAccess;

/**
 * 在分布式应用中请注意使用此类（自己实现存储方案，本方案是存在内存中的）
 * @author shuchaowen
 *
 */
public class MemoryWeiXinAccess extends WeiXinAccess{
	public MemoryWeiXinAccess(String appId, String appsecret) {
		super(appId, appsecret);
	}

	private WeiXinAccessInfo tokenInfo;
	private WeiXinAccessInfo ticketInfo;
	
	@Override
	public WeiXinAccessInfo refershAccessToken() {
		tokenInfo = getNewAccessToken();
		return tokenInfo;
	}

	@Override
	public WeiXinAccessInfo refreshAccessTicket() {
		ticketInfo = getNewAccessTicket();
		return ticketInfo;
	}

	@Override
	public WeiXinAccessInfo getCacheAccessToken() {
		return tokenInfo;
	}

	@Override
	public WeiXinAccessInfo getCacheAccessTicket() {
		return ticketInfo;
	}
}
