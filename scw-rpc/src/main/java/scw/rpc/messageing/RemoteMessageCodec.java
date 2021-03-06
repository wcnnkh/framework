package scw.rpc.messageing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.convert.TypeDescriptor;

public interface RemoteMessageCodec {
	void encode(OutputStream output, RemoteRequestMessage requestMessage) throws IOException, RemoteMessageCodecException;

	RemoteRequestMessage decode(InputStream input, MessageHeaders headers) throws IOException, RemoteMessageCodecException;
	
	void encode(OutputStream output, RemoteResponseMessage responseMessage) throws IOException, RemoteMessageCodecException;
	
	RemoteResponseMessage decode(InputStream input, MessageHeaders headers, TypeDescriptor responseType) throws IOException, RemoteMessageCodecException;
}
