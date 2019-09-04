package scw.mvc.http.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.utils.StringUtils;
import scw.mvc.FilterChain;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpFilter;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;

/**
 * 跨域
 * 
 * @author shuchaowen
 *
 */
public final class CrossDomainFilter extends HttpFilter {

	public final CrossDomainDefinition defaultDefinition;
	private Map<String, CrossDomainDefinition> crossDomainDefinitionMap = new HashMap<String, CrossDomainDefinition>();

	public CrossDomainFilter() {
		this(false);
	}

	public CrossDomainFilter(boolean credentials) {
		this("*", "*", -1, "*", credentials);
	}

	public CrossDomainFilter(String origin, String methods, int maxAge, String headers, boolean credentials) {
		this.defaultDefinition = new CrossDomainDefinition(origin, headers, methods, credentials, maxAge);
	}

	public CrossDomainFilter(CrossDomainDefinition crossDomainDefinition) {
		this.defaultDefinition = crossDomainDefinition;
	}

	public synchronized void register(String matchPath, String origin, String methods, int maxAge, String headers,
			boolean credentials) {
		crossDomainDefinitionMap.put(matchPath,
				new CrossDomainDefinition(origin, headers, methods, credentials, maxAge));
	}

	public CrossDomainDefinition getCrossDomainDefinition(String requestPath) {
		if (crossDomainDefinitionMap.isEmpty()) {
			return null;
		}

		for (Entry<String, CrossDomainDefinition> entry : crossDomainDefinitionMap.entrySet()) {
			if (StringUtils.test(requestPath, entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public Object doFilter(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse, FilterChain chain)
			throws Throwable {
		CrossDomainDefinition crossDomainDefinition = getCrossDomainDefinition(httpRequest.getRequestPath());
		if (crossDomainDefinition == null) {
			MVCUtils.responseCrossDomain(defaultDefinition, httpResponse);
		} else {
			MVCUtils.responseCrossDomain(crossDomainDefinition, httpResponse);
		}

		return chain.doFilter(channel);
	}
}
