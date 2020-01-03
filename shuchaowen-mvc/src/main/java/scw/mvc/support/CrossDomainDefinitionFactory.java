package scw.mvc.support;

import scw.mvc.http.HttpChannel;

public interface CrossDomainDefinitionFactory {
	CrossDomainDefinition getCrossDomainDefinition(HttpChannel httpChannel);
}
