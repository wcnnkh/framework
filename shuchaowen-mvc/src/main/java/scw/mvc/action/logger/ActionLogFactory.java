package scw.mvc.action.logger;

import java.util.Map;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Channel;
import scw.mvc.action.Action;

@AutoImpl(scw.mvc.action.logger.DefaultActionLogFactory.class)
public interface ActionLogFactory {
	String getIdentification(Action action, Channel channel) throws Exception;
	
	Map<String, String> getAttributeMap(Action action, Channel channel) throws Exception;
}
