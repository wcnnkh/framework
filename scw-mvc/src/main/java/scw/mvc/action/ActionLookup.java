package scw.mvc.action;

import scw.beans.annotation.AopEnable;
import scw.mvc.HttpChannel;

@AopEnable(false)
public interface ActionLookup {
	Action lookup(HttpChannel httpChannel);
}
