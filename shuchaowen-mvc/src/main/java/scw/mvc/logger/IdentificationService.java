package scw.mvc.logger;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Action;
import scw.mvc.Channel;

@AutoImpl(IdentificationServiceImpl.class)
public interface IdentificationService {
	String getIdentification(Action action, Channel channel);
}
