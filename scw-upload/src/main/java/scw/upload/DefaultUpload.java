package scw.upload;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import scw.core.utils.StringUtils;
import scw.core.utils.XTime;
import scw.io.FileUtils;

public class DefaultUpload implements Upload {
	private String rootPath;
	private String rootUrl;
	private final Set<String> exts = new HashSet<String>();
	private boolean requiredExt;
	private boolean appendYmd;

	public DefaultUpload(String rootPath, String rootUrl) {
		this.rootPath = StringUtils.cleanPath(rootPath);
		this.rootUrl = StringUtils.cleanPath(rootUrl);
	}

	public boolean isRequiredExt() {
		return requiredExt;
	}

	public void setRequiredExt(boolean requiredExt) {
		this.requiredExt = requiredExt;
	}

	public boolean isAppendYmd() {
		return appendYmd;
	}

	public void setAppendYmd(boolean appendYmd) {
		this.appendYmd = appendYmd;
	}

	public Set<String> getExts() {
		return exts;
	}

	public String upload(UploadItem uploadItem) throws IOException, UploadException {
		String ext = StringUtils.getFilenameExtension(uploadItem.getName());
		if (isRequiredExt()) {
			if (ext == null || !exts.contains(ext)) {
				throw new UploadException("允许使用的文件后缀名：(" + exts + ")");
			}
		}

		StringBuilder sb = new StringBuilder();
		if (appendYmd) {
			sb.append(XTime.format(System.currentTimeMillis(), "yyyy/MM/dd"));
			sb.append("/");
		}
		sb.append(uploadItem.getName());
		String path = sb.toString();

		FileUtils.copyInputStreamToFile(uploadItem.getBody(), new File(appendPath(rootPath, path)));
		return appendPath(rootUrl, path);
	}

	public static String appendPath(String path1, String path2) {
		String path = path2;
		if (StringUtils.isNotEmpty(path1)) {
			if (path1.endsWith("/")) {
				path = path1 + path;
			} else {
				path = path1 + "/" + path;
			}
		}
		return path;
	}
}
