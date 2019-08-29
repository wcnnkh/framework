package scw.mvc;

public interface ActionFactory {
	Action<Channel> getController(Channel channel);
}