package scw.mvc.action;

import scw.beans.annotation.Bean;
import scw.mvc.HttpChannel;

@Bean(proxy=false)
public interface ActionLookup {
	Action lookup(HttpChannel httpChannel);
}
