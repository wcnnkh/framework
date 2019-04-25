package scw.core.net.http.entity.parameter;

import java.nio.charset.Charset;

public abstract class AbstractMultipartParameter implements Parameter{
	public static final String BOUNDARY_TAG = "--";
	public static final String BR = "\r\n";
	
	private final Charset charset;
	private final String boundary;
	
	public AbstractMultipartParameter(Charset charset, String boundary){
		this.charset = charset;
		this.boundary = boundary;
	}

	public Charset getCharset() {
		return charset;
	}

	public String getBoundary() {
		return boundary;
	}
}
