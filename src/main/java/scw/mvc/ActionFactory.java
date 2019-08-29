package scw.mvc;

public interface ActionFactory {
	Action getController(Channel channel);
}