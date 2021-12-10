package io.basc.framework.messageing;

public interface FragmentMessage<T> extends Message<T> {
	boolean isLast();
}
