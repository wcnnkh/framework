package scw.rpc.web;

import java.util.concurrent.Callable;

import scw.convert.TypeDescriptor;
import scw.http.HttpResponseEntity;
import scw.http.MediaType;
import scw.http.client.HttpConnection;
import scw.io.UnsafeByteArrayInputStream;
import scw.io.UnsafeByteArrayOutputStream;
import scw.rpc.RemoteException;
import scw.rpc.messageing.RemoteMessageCodec;
import scw.rpc.messageing.RemoteRequestMessage;
import scw.rpc.messageing.RemoteResponseMessage;

public class HttpCallable implements Callable<Object> {
	private final TypeDescriptor responseType;
	private final HttpConnection httpConnection;
	private final RemoteMessageCodec messageCodec;
	private final RemoteRequestMessage requestMessage;

	public HttpCallable(HttpConnection httpConnection,
			RemoteMessageCodec messageCodec,
			RemoteRequestMessage requestMessage, TypeDescriptor responseType) {
		this.messageCodec = messageCodec;
		this.responseType = responseType;
		this.httpConnection = httpConnection;
		this.requestMessage = requestMessage;
	}

	public Object call() throws Exception {
		UnsafeByteArrayOutputStream output = new UnsafeByteArrayOutputStream();
		messageCodec.encode(output, requestMessage);
		HttpResponseEntity<byte[]> response = httpConnection
				.body(output.toByteArray())
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.execute(byte[].class);
		RemoteResponseMessage message = messageCodec.decode(
				new UnsafeByteArrayInputStream(response.getBody()),
				requestMessage, responseType);
		if (message.getThrowable() == null) {
			return message.getBody();
		}

		throw new RemoteException(httpConnection.getUrl().toString(),
				message.getThrowable());
	}

}
