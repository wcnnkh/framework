package scw.mvc.logger;

import java.util.Map;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Action;
import scw.mvc.Channel;

@AutoImpl(scw.mvc.logger.LoggerActionServiceImpl.class)
public interface LoggerActionService {
	String getIdentification(Action action, Channel channel) throws Exception;
	
	Map<String, String> getAttributeMap(Action action, Channel channel) throws Exception;
}
