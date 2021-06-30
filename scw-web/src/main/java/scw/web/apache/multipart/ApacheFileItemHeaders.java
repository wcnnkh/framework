package scw.web.apache.multipart;

import java.util.Iterator;

import org.apache.commons.fileupload.FileItemHeaders;

import scw.http.HttpHeaders;

public class ApacheFileItemHeaders extends HttpHeaders {
	private static final long serialVersionUID = 1L;

	public ApacheFileItemHeaders(FileItemHeaders fileItemHeaders) {
		if(fileItemHeaders == null) {
			return ;
		}
		Iterator<String> iterator = fileItemHeaders.getHeaderNames();
		while (iterator.hasNext()) {
			String name = iterator.next();
			Iterator<String> valueIterator = fileItemHeaders.getHeaders(name);
			while (valueIterator.hasNext()) {
				add(name, valueIterator.next());
			}
		}
	}
}
