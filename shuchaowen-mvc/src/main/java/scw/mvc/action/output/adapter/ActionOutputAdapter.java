package scw.mvc.action.output.adapter;

import scw.mvc.Channel;
import scw.mvc.action.output.ActionOutput;

public interface ActionOutputAdapter extends ActionOutput{
	boolean isAdapter(Channel channel, Object obj);
}
