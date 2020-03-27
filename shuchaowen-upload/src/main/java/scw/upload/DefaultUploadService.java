package scw.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import scw.beans.annotation.Bean;
import scw.core.GlobalPropertyFactory;
import scw.core.utils.StringUtils;
import scw.core.utils.XTime;
import scw.io.IOUtils;

@Bean(proxy = false)
public class DefaultUploadService implements UploadService {
	private String rootPath;
	private String rootUrl;

	public DefaultUploadService(String rootPath, String rootUrl) {
		this.rootPath = StringUtils.isEmpty(rootPath) ? GlobalPropertyFactory.getInstance().getWorkPath()
				: GlobalPropertyFactory.getInstance().format(rootPath, true);
		this.rootUrl = rootUrl == null ? "" : rootUrl;
	}

	public UploadResult upload(InputStream inputStream, String fileName) throws Exception {
		String ymd = XTime.format(System.currentTimeMillis(), "yyyy/MM/dd");
		File file = new File(rootPath + "/" + ymd);
		if (!file.exists()) {
			file.mkdirs();
		}

		String fileKey = ymd + "/" + fileName;
		file = new File(rootPath + "/" + fileKey);
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fos = null;
		long size = 0;
		try {
			fos = new FileOutputStream(file);
			size = IOUtils.write(inputStream, fos, 1024 * 1024);
		} catch (Exception e) {
			IOUtils.close(fos);
		}

		return new SimpleUploadResult(rootUrl.endsWith("/") ? (rootUrl + fileKey) : (rootUrl + "/" + fileKey), fileName,
				size);
	}

	public boolean delete(String url) {
		if (StringUtils.isEmpty(url)) {
			return false;
		}

		String[] arr = url.split("/");
		if (arr.length < 4) {
			return false;
		}

		StringBuilder key = new StringBuilder();
		for (int i = arr.length - 4; i < arr.length; i++) {
			key.append(arr[i]);
			if (i != arr.length - 1) {
				key.append("/");
			}
		}

		File file = new File(rootPath + "/" + key);
		if (!file.exists()) {
			return false;
		}
		return file.delete();
	}

}
