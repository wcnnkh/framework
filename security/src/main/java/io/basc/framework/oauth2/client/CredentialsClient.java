package io.basc.framework.oauth2.client;

import io.basc.framework.oauth2.AccessToken;

/**
 * 客户端模式（Client Credentials Grant）
 * 指客户端以自己的名义，而不是以用户的名义，向"服务提供商"进行认证。
 * 严格地说，客户端模式并不属于OAuth框架所要解决的问题 。 
 * 在这种模式中，用户直接向客户端注册，客户端以自己的名义要求"服务提供商"提供服务，其实不存在授权问题。
 * 
 * @author wcnnkh
 *
 */
public interface CredentialsClient extends RefreshAccessTokenClient {
	/**
	 * @param scope 表示权限范围，可选项。
	 * @return
	 */
	AccessToken getAccessToken(String scope);
}
