package scw.mvc.action;

import scw.beans.annotation.Bean;
import scw.mvc.HttpChannel;

@Bean(proxy=false)
public interface ActionFilter {
	Object doFilter(HttpChannel httpChannel, Action action, ActionService service) throws Throwable;
}
