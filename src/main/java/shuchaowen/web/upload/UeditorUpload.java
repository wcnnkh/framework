package shuchaowen.web.upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import shuchaowen.common.exception.ShuChaoWenRuntimeException;
import shuchaowen.common.utils.FileUtils;
import shuchaowen.web.servlet.Request;
import shuchaowen.web.upload.ueditor.ActionEnter;

public final class UeditorUpload implements Upload {
	private final String rootPath;
	private final String jsonConfigPath;

	public UeditorUpload(String rootPath, String jsonConfigPath) {
		this.rootPath = rootPath;
		this.jsonConfigPath = jsonConfigPath;
		try {
			init();
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
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

	public void execute(Request request) {
		ActionEnter actionEnter = new ActionEnter(request, rootPath);
		try {
			request.getResponse().write(actionEnter.exec());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
