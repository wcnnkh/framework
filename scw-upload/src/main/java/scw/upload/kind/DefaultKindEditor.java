package scw.upload.kind;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XTime;
import scw.io.FileUtils;
import scw.upload.UploadItem;
import scw.util.comparator.FileComparator;

public class DefaultKindEditor extends AbstractKindUpload {
	private final String rootPath;
	private final String rootUrl;

	public DefaultKindEditor(String rootPath, String rootUrl) {
		this.rootPath = StringUtils.cleanPath(rootPath);
		this.rootUrl = StringUtils.cleanPath(rootUrl);
	}

	public KindManagerResult manager(String group, KindDirType dir, String path, KindOrderType orderType) {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotEmpty(group)) {
			sb.append(group).append("/");
		}
		sb.append(dir).append("/");
		if (StringUtils.isNotEmpty(path)) {
			sb.append(path);
		}

		String pathToUse = sb.toString();
		String currentDirPath = path == null ? "" : path;
		String currentUrl = StringUtils.mergePath(rootUrl, pathToUse);
		String moveupDirPath = "";
		if (StringUtils.isNotEmpty(path)) {
			String str = currentDirPath.substring(0, currentDirPath.length() - 1);
			moveupDirPath = str.lastIndexOf("/") >= 0 ? str.substring(0, str.lastIndexOf("/") + 1) : "";
		}

		List<KindFileItem> fileList = list(new File(StringUtils.mergePath(rootPath, pathToUse)), orderType);

		KindManagerResult kindManagerResult = new KindManagerResult();
		kindManagerResult.setFile_list(fileList);
		kindManagerResult.setCurrent_dir_path(currentDirPath);
		kindManagerResult.setMoveup_dir_path(moveupDirPath);
		kindManagerResult.setCurrent_url(currentUrl);
		kindManagerResult.setTotal_count(fileList.size());
		return kindManagerResult;
	}

	private List<KindFileItem> list(File rootFile, KindOrderType type) {
		if (!rootFile.exists()) {
			return Collections.emptyList();
		}

		File[] files = rootFile.listFiles();
		if (ArrayUtils.isEmpty(files)) {
			return Collections.emptyList();
		}

		switch (type) {
		case NAME:
			Arrays.sort(files, FileComparator.NAME);
			break;
		case SIZE:
			Arrays.sort(files, FileComparator.SIZE);
			break;
		case TYPE:
			Arrays.sort(files, FileComparator.TYPE);
		default:
			break;
		}

		List<KindFileItem> list = new ArrayList<KindFileItem>();
		for (File file : files) {
			KindFileItem item = new KindFileItem();
			item.setDir(file.isDirectory());
			item.setHasFile(file.isDirectory() && !ArrayUtils.isEmpty(file.list()));
			item.setSize(file.length());
			item.setPhoto(isImage(file.getName()));
			item.setExt(StringUtils.getFilenameExtension(file.getName()));
			item.setName(file.getName());
			item.setDateTime(file.lastModified());
			list.add(item);
		}
		return list;
	}

	@Override
	protected String uploadInternal(String group, KindDirType dir, UploadItem item) throws IOException {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotEmpty(group)) {
			sb.append(group).append("/");
		}
		sb.append(dir).append("/");
		sb.append(XTime.format(System.currentTimeMillis(), "yyyy/MM/dd"));
		sb.append("/");
		sb.append(item.getName());
		String path = sb.toString();

		String filePath = path;
		if (StringUtils.isNotEmpty(rootPath)) {
			if (rootPath.endsWith("/")) {
				filePath = rootPath + filePath;
			} else {
				filePath = rootPath + "/" + filePath;
			}
		}

		File file = new File(filePath);
		FileUtils.copyInputStreamToFile(item.getBody(), file);

		String url = path;
		if (StringUtils.isNotEmpty(rootUrl)) {
			if (rootUrl.endsWith("/")) {
				url = rootUrl + url;
			} else {
				url = rootUrl + "/" + url;
			}
		}
		return url;
	}
}
