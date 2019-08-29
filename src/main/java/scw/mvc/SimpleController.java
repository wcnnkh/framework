package scw.mvc;

public class SimpleController implements Controller {
	private ActionFactory actionFactory;

	public SimpleController(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}

	public void doController(Channel channel) throws Throwable {
		Action<Channel> action = actionFactory.getController(channel);
		action.doAction(channel);
	}

}
