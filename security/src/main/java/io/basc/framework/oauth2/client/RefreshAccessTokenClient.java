package io.basc.framework.oauth2.client;

import io.basc.framework.oauth2.AccessToken;

public interface RefreshAccessTokenClient {
	/**
	 * 如果用户访问的时候，客户端的"访问令牌"已经过期，则需要使用"更新令牌"申请一个新的访问令牌。
	 * @param accessToken
	 * @param scope
	 *            表示申请的授权范围，不可以超出上一次申请的范围，如果省略该参数，则表示与上一次一致
	 * @return
	 */
	AccessToken refreshAccessToken(AccessToken accessToken, String scope);
}
