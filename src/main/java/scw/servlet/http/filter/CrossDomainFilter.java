package scw.servlet.http.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.annotation.Bean;
import scw.core.utils.StringUtils;
import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.ServletUtils;

/**
 * 跨域
 * 
 * @author shuchaowen
 *
 */
@Bean(proxy = false)
public class CrossDomainFilter implements Filter {
	private static final CrossDomainDefinition DEFAULT = new CrossDomainDefinition("*", "*", "*", false, -1);
	private Map<String, CrossDomainDefinition> crossDomainDefinitionMap = new HashMap<String, CrossDomainDefinition>();

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

	public void doFilter(Request request, Response response, FilterChain filterChain) throws Throwable {
		if (!ServletUtils.isHttpServlet(request, response)) {
			filterChain.doFilter(request, response);
			return;
		}

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		CrossDomainDefinition crossDomainDefinition = getCrossDomainDefinition(httpServletRequest.getServletPath());
		if (crossDomainDefinition == null) {
			DEFAULT.write(httpServletResponse);
		} else {
			crossDomainDefinition.write(httpServletResponse);
		}
		filterChain.doFilter(request, response);
	}
}
