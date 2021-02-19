package scw.mvc.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import scw.http.MediaType;
import scw.http.server.ServerHttpResponse;
import scw.io.IOUtils;
import scw.io.Resource;
import scw.mapper.MapperUtils;
import scw.mvc.HttpChannel;
import scw.net.FileMimeTypeUitls;
import scw.net.MimeType;

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
		setResponseFileDisposition(resource.getFilename(), httpChannel.getResponse());

		InputStream is = null;
		OutputStream os = null;
		try {
			os = httpChannel.getResponse().getBody();
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
		return MapperUtils.getMapper().getFields(DownloadView.class).getValueMap(this).toString();
	}
}
