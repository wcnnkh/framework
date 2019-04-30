package scw.core.net.http.client;

import java.nio.charset.Charset;

import scw.core.Constants;

/**
 * TODO 还未完成
 * 
 * @author shuchaowen
 *
 */
public class DefaultHttpClient extends AbstractHttpClient {
	private final CookieManager cookieManager;
	private final Charset charset;

	public DefaultHttpClient(boolean debug) {
		this(new MemoryCookieManager(debug), Constants.DEFAULT_CHARSET);
	}

	public DefaultHttpClient(CookieManager cookieManager, Charset charset) {
		this.cookieManager = cookieManager;
		this.charset = charset;
	}

	@Override
	protected Charset getCharset() {
		return charset;
	}

	@Override
	protected CookieManager getCookieManager() {
		return cookieManager;
	}

}
