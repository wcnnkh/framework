package scw.event;

public interface NamedEventListener<T extends NamedEvent> extends EventListener<T>{
	String getName();
}
