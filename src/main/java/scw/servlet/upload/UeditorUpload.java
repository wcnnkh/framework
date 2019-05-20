package scw.servlet.upload;

import java.io.File;
import java.io.FileNotFoundException;

import scw.core.utils.FileUtils;
import scw.servlet.http.HttpRequest;
import scw.servlet.http.HttpResponse;
import scw.servlet.upload.ueditor.ActionEnter;

public final class UeditorUpload implements Upload {
	private final String rootPath;
	private final String jsonConfigPath;

	public UeditorUpload(String rootPath, String jsonConfigPath) {
		this.rootPath = rootPath;
		this.jsonConfigPath = jsonConfigPath;
		try {
			init();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void init() throws Exception {
		File file = new File(rootPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		File configFile = new File(rootPath + "config.json");
		if (configFile.exists()) {
			configFile.delete();
		}

		File myConfigPath = new File(jsonConfigPath);
		if (!myConfigPath.exists()) {
			throw new FileNotFoundException("not found ueditor config.json");
		}
		FileUtils.copyFileUsingFileChannels(myConfigPath, configFile);
	}

	public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
		ActionEnter actionEnter = new ActionEnter(httpRequest, rootPath);
		httpResponse.write(actionEnter.exec());
	}

}
