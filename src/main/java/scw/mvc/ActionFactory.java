package scw.mvc;

import scw.beans.annotation.AutoImpl;
import scw.mvc.support.DefaultHttpActionFactory;

@AutoImpl({DefaultHttpActionFactory.class})
public interface ActionFactory {
	Action getAction(Channel channel);
}
