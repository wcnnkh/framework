package io.basc.framework.net.message;

public interface Entity<T> extends Message {
	T getBody();
}
