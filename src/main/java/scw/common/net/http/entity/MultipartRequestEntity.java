package scw.common.net.http.entity;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

import scw.common.net.Request;
import scw.common.net.RequestEntity;
import scw.common.net.http.entity.file.File;
import scw.common.net.http.entity.parameter.AbstractMultipartParameter;
import scw.common.net.http.entity.parameter.FieldMultipartParameter;
import scw.common.net.http.entity.parameter.FileMultipartParameter;

public class MultipartRequestEntity extends ArrayList<AbstractMultipartParameter> implements RequestEntity {
	public static final String DEFAULT_BOUNDARY = "----WebKitFormBoundaryKSD2ndz6G9RPNjx0";
	private static final long serialVersionUID = 1L;
	private final Charset charset;
	private final String boundary;
	
	public MultipartRequestEntity(Charset charset){
		this(charset, DEFAULT_BOUNDARY);
	}

	public MultipartRequestEntity(Charset charset, String boundary){
		this.charset = charset;
		this.boundary = boundary;
	}
	
	public Charset getCharset() {
		return charset;
	}

	public String getBoundary() {
		return boundary;
	}
	
	public MultipartRequestEntity addFile(File file){
		add(new FileMultipartParameter(charset, boundary, file));
		return this;
	}
	
	public MultipartRequestEntity addField(String name, Object value){
		if(name == null || value == null || name.length() == 0){
			return this;
		}
		
		add(new FieldMultipartParameter(charset, boundary, name, value.toString()));
		return this;
	}

	public void write(Request request) throws IOException {
		if(request.getRequestProperty("Charset") == null){
			request.setRequestProperty("Charset", charset.name());
		}
		
		if(request.getRequestProperty("Content-Type") == null){
			request.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		}
		
		OutputStream out = request.getOutputStream();
		if(isEmpty()){
			Iterator<AbstractMultipartParameter> iterator = iterator();
			while(iterator().hasNext()){
				iterator.next().write(out);
			}
			end(out);
		}
	}
	
	private void end(OutputStream out) throws IOException{
		StringBuilder end = new StringBuilder();
		end.append(AbstractMultipartParameter.BR);
		end.append(AbstractMultipartParameter.BOUNDARY_TAG);
		end.append(boundary);
		end.append(AbstractMultipartParameter.BOUNDARY_TAG);
		end.append(AbstractMultipartParameter.BR);
		out.write(end.toString().getBytes(charset));
	}
}
