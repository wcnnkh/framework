package scw.core.net.http.client;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.net.http.client.cookie.DefaultCookie;
import scw.core.utils.StringUtils;

public final class MemoryCookieManager implements CookieManager {
	protected static Logger logger = LoggerFactory.getLogger(MemoryCookieManager.class);
	private final boolean debug;

	public MemoryCookieManager(boolean debug) {
		this.debug = debug;
	}

	private Map<String, PathCookie> cookieMap = new ConcurrentHashMap<String, PathCookie>(4, 1);

	private String getDomain(URL url) {
		if (url.getPort() == -1 || url.getPort() == url.getDefaultPort()) {
			return url.getHost();
		} else {
			return url.getHost() + ":" + url.getPort();
		}
	}

	public String getCookie(URL url) {
		String domain = getDomain(url);
		StringBuilder sb = new StringBuilder();
		for (Entry<String, PathCookie> entry : cookieMap.entrySet()) {
			if (!domain.endsWith(entry.getKey())) {
				continue;
			}

			String cookie = entry.getValue().getCookie(url);
			if (StringUtils.isEmpty(cookie)) {
				continue;
			}

			if (sb.length() != 0) {
				sb.append("; ");
			}

			sb.append(cookie);
		}

		if (debug) {
			if (sb.length() > 0) {
				logger.debug("GET [{}] cookie: {}", url.toString(), sb.toString());
			}
		}
		return sb.toString();
	}

	public void setCookie(URL url, String cookies) {
		if (debug) {
			logger.debug("SET [{}] cookie: {}", url.toString(), cookies);
		}

		DefaultCookie setCookie = new DefaultCookie(url, cookies);
		boolean find = false;
		for (Entry<String, PathCookie> entry : cookieMap.entrySet()) {
			if (!setCookie.getDomain().endsWith(entry.getKey())) {
				continue;
			}

			find = true;
			entry.getValue().setCookie(setCookie);
		}

		if (!find) {
			PathCookie pathCookie = new PathCookie();
			if (cookieMap.putIfAbsent(setCookie.getDomain(), pathCookie) == null) {
				pathCookie.setCookie(setCookie);
			} else {
				setCookie(url, cookies);
			}
		}
	}
}

class PathCookie {
	private Map<String, Map<String, DefaultCookie>> pathCookieMap = new ConcurrentHashMap<String, Map<String, DefaultCookie>>(
			2, 1);

	public String getCookie(URL url) {
		String path = url.getPath();
		if (StringUtils.isEmpty(path)) {
			path = "/";
		}

		StringBuilder sb = new StringBuilder();
		for (Entry<String, Map<String, DefaultCookie>> entry : pathCookieMap.entrySet()) {
			if (!path.startsWith(entry.getKey())) {
				continue;
			}

			for (Entry<String, DefaultCookie> ce : entry.getValue().entrySet()) {
				DefaultCookie cookie = ce.getValue();
				if (!cookie.isEffective()) {
					entry.getValue().remove(cookie.getName());
					continue;
				}

				if (sb.length() != 0) {
					sb.append("; ");
				}
				sb.append(cookie.getCookie());
			}
		}
		return sb.toString();
	}

	public void setCookie(DefaultCookie cookie) {
		for (Entry<String, Map<String, DefaultCookie>> entry : pathCookieMap.entrySet()) {
			if (!cookie.getPath().startsWith(entry.getKey())) {
				continue;
			}

			entry.getValue().remove(cookie.getName());
		}

		if (cookie.isEffective()) {
			setCasCookie(cookie);
		}
	}

	private void setCasCookie(DefaultCookie cookie) {
		Map<String, DefaultCookie> map = pathCookieMap.get(cookie.getPath());
		if (map == null) {
			if (pathCookieMap.putIfAbsent(cookie.getPath(), new HashMap<String, DefaultCookie>()) == null) {
				setCasCookie(cookie);
				return;
			}
		}
		map.put(cookie.getName(), cookie);
	}
}
