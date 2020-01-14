package scw.net.http;

import java.io.Closeable;

import scw.net.message.InputMessage;

public interface ClientHttpResponse extends InputMessage, Closeable{
	int getResponseCode();

	String getResponseMessage();
}
