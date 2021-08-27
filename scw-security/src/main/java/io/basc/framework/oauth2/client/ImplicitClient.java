package io.basc.framework.oauth2.client;

import io.basc.framework.oauth2.AccessToken;

import java.net.URL;

/**
 * 简化模式（implicit grant type）不通过第三方应用程序的服务器，直接在浏览器中向认证服务器申请令牌，跳过了"授权码"这个步骤，因此得名。<br/>
 * 所有步骤在浏览器中完成，令牌对访问者是可见的，且客户端不需要认证。
 * @author shuchaowen
 *
 */
public interface ImplicitClient {
	String getClientId();
	
	URL getAuthorizeURL(String redirect_uri, String scope, String state);
	
	AccessToken analysis(URL url);
}