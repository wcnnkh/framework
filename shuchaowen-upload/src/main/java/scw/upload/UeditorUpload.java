package scw.upload;

import java.io.File;
import java.io.FileNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.io.FileUtils;
import scw.upload.ueditor.ActionEnter;

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

	public void execute(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		ActionEnter actionEnter = new ActionEnter(httpRequest, rootPath);
		httpResponse.getWriter().write(actionEnter.exec());
	}

}
