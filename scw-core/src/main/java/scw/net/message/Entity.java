package scw.net.message;

public interface Entity<T> extends Message {
	T getBody();
}
