package scw.mvc.action;

import scw.mvc.HttpChannel;

public interface ActionLookup {
	Action lookup(HttpChannel httpChannel);
}
