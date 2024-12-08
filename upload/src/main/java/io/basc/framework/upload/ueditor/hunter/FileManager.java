package io.basc.framework.upload.ueditor.hunter;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import io.basc.framework.upload.ueditor.PathFormat;
import io.basc.framework.upload.ueditor.define.AppInfo;
import io.basc.framework.upload.ueditor.define.BaseState;
import io.basc.framework.upload.ueditor.define.MultiState;
import io.basc.framework.upload.ueditor.define.State;
import io.basc.framework.util.io.FileUtils;
import io.basc.framework.util.io.SuffixFileFilter;

public class FileManager {

	private String dir = null;
	private String rootPath = null;
	private String[] allowFiles = null;
	private int count = 0;

	public FileManager(Map<String, Object> conf) {

		this.rootPath = (String) conf.get("rootPath");
		this.dir = this.rootPath + (String) conf.get("dir");
		this.allowFiles = this.getAllowFiles(conf.get("allowFiles"));
		this.count = (Integer) conf.get("count");

	}

	public State listFile(int index) {

		File dir = new File(this.dir);
		State state = null;

		if (!dir.exists()) {
			return new BaseState(false, AppInfo.NOT_EXIST);
		}

		if (!dir.isDirectory()) {
			return new BaseState(false, AppInfo.NOT_DIRECTORY);
		}

		Collection<File> list = FileUtils.stream(dir, (FileFilter) new SuffixFileFilter(this.allowFiles))
				.map((file) -> file.getFile()).collect(Collectors.toList());
		if (index < 0 || index > list.size()) {
			state = new MultiState(true);
		} else {
			Object[] fileList = Arrays.copyOfRange(list.toArray(), index, index + this.count);
			state = this.getState(fileList);
		}

		state.putInfo("start", index);
		state.putInfo("total", list.size());
		return state;

	}

	private State getState(Object[] files) {

		MultiState state = new MultiState(true);
		BaseState fileState = null;

		File file = null;

		for (Object obj : files) {
			if (obj == null) {
				break;
			}
			file = (File) obj;
			fileState = new BaseState(true);
			String url = PathFormat.format(this.getPath(file));
			fileState.putInfo("url", url.substring(rootPath.length() - 1));
			state.addState(fileState);
		}

		return state;

	}

	private String getPath(File file) {

		String path = file.getAbsolutePath();

		return path.replace(this.rootPath, "/");

	}

	private String[] getAllowFiles(Object fileExt) {

		String[] exts = null;
		String ext = null;

		if (fileExt == null) {
			return new String[0];
		}

		exts = (String[]) fileExt;

		for (int i = 0, len = exts.length; i < len; i++) {

			ext = exts[i];
			exts[i] = ext.replace(".", "");

		}

		return exts;

	}

}
