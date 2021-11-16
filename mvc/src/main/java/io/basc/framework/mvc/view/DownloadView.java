package io.basc.framework.mvc.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import io.basc.framework.http.MediaType;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.mvc.HttpChannel;
import io.basc.framework.net.FileMimeTypeUitls;
import io.basc.framework.net.MimeType;
import io.basc.framework.web.ServerHttpResponse;

public final class DownloadView implements View {
	private String encoding;
	private Resource resource;

	public DownloadView(Resource resource) {
		this.resource = resource;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void render(HttpChannel httpChannel) throws IOException {
		MimeType mimeType = FileMimeTypeUitls.getMimeType(resource);
		if(mimeType.getCharset() == null && encoding != null){
			mimeType = new MediaType(mimeType, encoding);
		}
		httpChannel.getResponse().setContentType(mimeType);
		setResponseFileDisposition(resource.getName(), httpChannel.getResponse());

		InputStream is = null;
		OutputStream os = null;
		try {
			os = httpChannel.getResponse().getOutputStream();
			is = resource.getInputStream();
			IOUtils.write(is, os, 1024);
		} finally {
			IOUtils.close(is, os);
		}
	}
	
	public static void setResponseFileDisposition(String fileName, ServerHttpResponse serverHttpResponse) throws UnsupportedEncodingException{
		fileName = new String(fileName.getBytes(), "iso-8859-1");
		serverHttpResponse.getHeaders().set("Content-Disposition", "attachment;filename=" + fileName);
	}
	
	@Override
	public String toString() {
		return MapperUtils.toString(this);
	}
}
