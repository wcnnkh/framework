package io.basc.framework.oauth2.client;

import io.basc.framework.oauth2.AccessToken;

/**
 * 授权码模式（authorization code）是功能最完整、流程最严密的授权模式。
 * 它的特点就是通过客户端的后台服务器，与"服务提供商"的认证服务器进行互动。
 * 
 * @author wcnnkh
 *
 */
public interface AuthorizationCodeClient extends RefreshAccessTokenClient {
	String getClientId();

	AccessToken getAccessToken(String code, String redirect_uri);
}
