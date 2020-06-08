package scw.event;

public interface NamedEventListener<T extends Event> extends EventListener<T>{
	String getName();
}
