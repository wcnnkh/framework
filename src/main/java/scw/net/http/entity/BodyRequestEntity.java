package scw.net.http.entity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import scw.net.Request;
import scw.net.RequestEntity;

public class BodyRequestEntity implements RequestEntity{
	private final ByteBuffer byteBuffer;
	
	public BodyRequestEntity(String body, Charset charset){
		this.byteBuffer = charset.encode(body);
	}
	
	public BodyRequestEntity(ByteBuffer byteBuffer){
		this.byteBuffer = byteBuffer;
	}
	
	public void write(Request request) throws IOException {
		request.getOutputStream().write(byteBuffer.array());
	}

	public ByteBuffer getByteBuffer() {
		return byteBuffer;
	}
}
