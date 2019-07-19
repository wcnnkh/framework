package scw.net.http.client;

import scw.core.Constants;

public class DefaultHttpClient extends AbstractHttpClient {
	private final CookieManager cookieManager;
	private final String charsetName;

	public DefaultHttpClient(boolean debug) {
		this(new MemoryCookieManager(debug), Constants.DEFAULT_CHARSET_NAME);
	}

	public DefaultHttpClient(CookieManager cookieManager, String charsetName) {
		this.cookieManager = cookieManager;
		this.charsetName = charsetName;
	}

	@Override
	protected String getCharsetName() {
		return charsetName;
	}

	@Override
	protected CookieManager getCookieManager() {
		return cookieManager;
	}

}
