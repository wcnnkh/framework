package scw.net;

public interface HttpMessage extends Message {
	int getCode();

	String getMessage();
}
