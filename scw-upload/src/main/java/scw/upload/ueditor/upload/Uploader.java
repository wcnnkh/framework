package scw.upload.ueditor.upload;

import java.util.Map;

import scw.http.server.MultiPartServerHttpRequest;
import scw.upload.ueditor.define.State;

public class Uploader {
	private MultiPartServerHttpRequest request = null;
	private Map<String, Object> conf = null;

	public Uploader(MultiPartServerHttpRequest request, Map<String, Object> conf) {
		this.request = request;
		this.conf = conf;
	}

	public final State doExec() {
		String filedName = (String) this.conf.get("fieldName");
		State state = null;

		if ("true".equals(this.conf.get("isBase64"))) {
			state = Base64Uploader.save(this.request.getParameterMap().getFirst(filedName),
					this.conf);
		} else {
			state = BinaryUploader.save(this.request, this.conf);
		}

		return state;
	}
}
