package shuchaowen.core.http.rpc;

import java.io.IOException;
import java.io.OutputStream;

import shuchaowen.core.http.client.parameter.Parameter;
import shuchaowen.core.http.rpc.serialization.Serializer;

public class ObjectParameter implements Parameter{
	private Serializer serializer;
	private Message message;
	
	public ObjectParameter(Serializer serializer, Message message){
		this.serializer = serializer;
		this.message = message;
	}
	
	public void wrapper(OutputStream outputStream) throws IOException {
		serializer.encode(outputStream, message);
	}
	
}
