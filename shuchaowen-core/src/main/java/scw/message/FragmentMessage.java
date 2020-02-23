package scw.message;


public interface FragmentMessage<T> extends Message<T>{
	boolean isLast();
}
