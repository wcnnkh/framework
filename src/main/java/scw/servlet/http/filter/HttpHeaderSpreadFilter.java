package scw.servlet.http.filter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.ServletUtils;

@SuppressWarnings("unchecked")
public final class HttpHeaderSpreadFilter extends LinkedList<String> implements Filter {
	private static final long serialVersionUID = 1L;
	{
		add("Cookie");
	}

	public void doFilter(Request request, Response response, FilterChain filterChain) throws Throwable {
		if (request instanceof HttpServletRequest) {
			for (String name : this) {
				setHeader(name, ((HttpServletRequest) request).getHeader(name));
			}
		}
		filterChain.doFilter(request, response);
	}

	public static String getHeader(String name) {
		Map<String, String> headMap = (Map<String, String>) ServletUtils
				.getControllerThreadLocalResource(HttpHeaderSpreadFilter.class);
		return headMap == null ? null : headMap.get(name);
	}

	public static Map<String, String> getheaderMap() {
		HashMap<String, String> headMap = (HashMap<String, String>) ServletUtils
				.getControllerThreadLocalResource(HttpHeaderSpreadFilter.class);
		return (Map<String, String>) (headMap == null ? null : headMap.clone());
	}

	public static void setHeader(String name, String value) {
		Map<String, String> headMap = (Map<String, String>) ServletUtils
				.getControllerThreadLocalResource(HttpHeaderSpreadFilter.class);
		if (headMap == null) {
			headMap = new HashMap<String, String>(8);
			ServletUtils.bindControllerThreadLocalResource(HttpHeaderSpreadFilter.class, headMap);
		}
		headMap.put(name, value);
	}
}
