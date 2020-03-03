package scw.mvc.action.output;

import java.util.LinkedList;

import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.output.adapter.ActionOutputAdapter;

public class MultiAdapterActionOutput extends LinkedList<ActionOutputAdapter>
		implements ActionOutput {
	private static final long serialVersionUID = 1L;

	public void output(Channel channel, Action action, Object obj)
			throws Throwable {
		for (ActionOutputAdapter adapter : this) {
			if (adapter.isAdapter(channel, obj)) {
				adapter.output(channel, action, obj);
				return;
			}
		}
	}
}
