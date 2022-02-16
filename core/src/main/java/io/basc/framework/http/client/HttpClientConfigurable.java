package io.basc.framework.http.client;

import java.net.CookieHandler;

import io.basc.framework.lang.Nullable;

public interface HttpClientConfigurable<T extends HttpClientConfigurable<T>> extends Cloneable {
	ClientHttpRequestFactory getRequestFactory();

	T setRequestFactory(ClientHttpRequestFactory requestFactory);

	@Nullable
	CookieHandler getCookieHandler();

	T setCookieHandler(@Nullable CookieHandler cookieHandler);

	@Nullable
	RedirectManager getRedirectManager();

	T setRedirectManager(@Nullable RedirectManager redirectManager);

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
