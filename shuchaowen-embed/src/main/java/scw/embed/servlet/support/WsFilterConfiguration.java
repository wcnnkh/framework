package scw.embed.servlet.support;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.Filter;

import org.apache.tomcat.websocket.server.WsFilter;

import scw.core.instance.annotation.Configuration;
import scw.embed.servlet.FilterConfiguration;

@Configuration
public class WsFilterConfiguration implements FilterConfiguration {
	private static final Filter wsFilter = new WsFilter();
	private static final String url = "/*";

	public String getName() {
		return WsFilter.class.getSimpleName();
	}

	public Collection<String> getURLPatterns() {
		return Arrays.asList(url);
	}

	public Collection<? extends Filter> getFilters() {
		return Arrays.asList(wsFilter);
	}
}
