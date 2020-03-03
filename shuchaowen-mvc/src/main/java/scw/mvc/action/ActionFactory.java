package scw.mvc.action;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Channel;
import scw.mvc.action.http.DefaultHttpActionFactory;

@AutoImpl({DefaultHttpActionFactory.class})
public interface ActionFactory {
	Action getAction(Channel channel);
}
