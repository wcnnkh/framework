package scw.mvc.action.output;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Channel;
import scw.mvc.action.Action;

@AutoImpl(ConfigurationActionOutput.class)
public interface ActionOutput{
	void output(Channel channel, Action action, Object obj) throws Throwable;
}
