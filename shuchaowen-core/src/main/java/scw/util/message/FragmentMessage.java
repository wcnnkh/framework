package scw.util.message;


public interface FragmentMessage<T> extends Message<T>{
	boolean isLast();
}
