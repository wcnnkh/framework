package scw.net.message.converter;

import java.util.LinkedList;

import scw.net.mime.MimeType;
import scw.net.mime.MimeTypeUtils;

public class SupportMimeTypes<T extends MimeType> extends LinkedList<T> {
	private static final long serialVersionUID = 1L;

	public boolean canRead(T contentType) {
		if (contentType == null) {
			return true;
		}
		for (MimeType supportContentType : this) {
			// 是否包含
			if (supportContentType.includes(contentType)) {
				return true;
			}
		}
		return false;
	}

	public boolean canWrite(T contentType) {
		if (contentType == null || MimeTypeUtils.ALL.equals(contentType)) {
			return true;
		}
		for (MimeType supportContentType : this) {
			// 是否兼容
			if (supportContentType.isCompatibleWith(contentType)) {
				return true;
			}
		}
		return false;
	}

	public void add(T... mimeTypes) {
		if (mimeTypes == null) {
			return;
		}

		for (T t : mimeTypes) {
			add(t);
		}
	}
}
