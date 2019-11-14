package scw.mvc.support;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Action;
import scw.mvc.Channel;

@AutoImpl({DefaultHttpActionFactory.class})
public interface ActionFactory {
	Action getAction(Channel channel);
}
