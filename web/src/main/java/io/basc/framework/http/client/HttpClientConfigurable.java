package io.basc.framework.http.client;

import java.net.CookieHandler;

public interface HttpClientConfigurable<T extends HttpClientConfigurable<T>> extends Cloneable {
	ClientHttpRequestFactory getRequestFactory();

	T setRequestFactory(ClientHttpRequestFactory requestFactory);

	CookieHandler getCookieHandler();

	T setCookieHandler(CookieHandler cookieHandler);

	RedirectManager getRedirectManager();

	T setRedirectManager(RedirectManager redirectManager);

	/**
	 * 设置最大的redirect深度
	 * 
	 * @param maxDeep -1表示永久, 0表示禁止
	 * @return
	 */
	default T setMaxRedirectDeep(long maxDeep) {
		return setRedirectManager(new DefaultHttpRedirectManager(maxDeep));
	}

	/**
	 * 克隆
	 * 
	 * @return
	 */
	T clone();
}
