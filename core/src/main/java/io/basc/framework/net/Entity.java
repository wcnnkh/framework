package io.basc.framework.net;

public interface Entity<T> extends Message {
	T getBody();
}
