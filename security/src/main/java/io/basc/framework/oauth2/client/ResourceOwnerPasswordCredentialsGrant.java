package io.basc.framework.oauth2.client;

import io.basc.framework.oauth2.AccessToken;

/**
 * 密码模式（Resource Owner Password Credentials Grant）中，用户向客户端提供自己的用户名和密码。<br/>
 * 客户端使用这些信息，向"服务商提供商"索要授权。<br/>
 * 在这种模式中，用户必须把自己的密码给客户端，但是客户端不得储存密码。<br/>
 * 这通常用在用户对客户端高度信任的情况下，比如客户端是操作系统的一部分，或者由一个著名公司出品。<br/>
 * 而认证服务器只有在其他授权模式无法执行的情况下，才能考虑使用这种模式。
 * 
 * @author shuchaowen
 *
 */
public interface ResourceOwnerPasswordCredentialsGrant extends
		RefreshAccessTokenClient {
	AccessToken getAccessToken(String username, String password, String scope);
}
