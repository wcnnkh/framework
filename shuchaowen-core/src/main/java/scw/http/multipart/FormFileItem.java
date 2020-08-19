package scw.http.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import scw.compatible.CompatibleUtils;
import scw.http.ContentDisposition;
import scw.http.HttpUtils;
import scw.http.MediaType;
import scw.io.UnsafeByteArrayInputStream;
import scw.json.JSONSupport;
import scw.value.ValueUtils;

class FormFileItem extends FileItem {
	private String body;

	public FormFileItem(String name, Object body, Charset charset, JSONSupport jsonSupport) {
		super(name);
		getHeaders().setContentDisposition(ContentDisposition.builder("form-data").name(name).build());
		if (ValueUtils.isBaseType(body.getClass())) {
			this.body = body.toString();
			getHeaders().setContentType(new MediaType(MediaType.TEXT_HTML, charset));
		} else {
			this.body = HttpUtils.toJsonString(body, jsonSupport);
			getHeaders().setContentType(new MediaType(MediaType.APPLICATION_JSON, charset));
		}
		getHeaders().setContentLength(getBytes().length);
	}
	
	public byte[] getBytes(){
		return CompatibleUtils.getStringOperations().getBytes(this.body, getCharset());
	}

	public InputStream getBody() {
		return new UnsafeByteArrayInputStream(getBytes());
	}

	public void close() throws IOException {
		// ignore
	}

	@Override
	public String getTextBody() throws IOException {
		return body;
	}
}
