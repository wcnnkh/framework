package io.basc.framework.upload.kind;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.basc.framework.io.FileUtils;
import io.basc.framework.io.FilenameUtils;
import io.basc.framework.net.message.multipart.MultipartMessage;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.TimeUtils;
import io.basc.framework.util.comparator.FileComparator;

public class DefaultKindEditor extends AbstractKindUpload {
	private final String rootPath;
	private final String rootUrl;

	public DefaultKindEditor(String rootPath, String rootUrl) {
		this.rootPath = StringUtils.cleanPath(rootPath);
		this.rootUrl = StringUtils.cleanPath(rootUrl);
	}

	@Override
	protected String getRootUrl(KindDirType dir) {
		return rootUrl;
	}

	@Override
	protected List<KindFileItem> managerInternal(String group, KindDirType dir, String path, KindOrderType orderType) {
		String filePath = StringUtils.mergePaths(rootPath, path);
		filePath = FilenameUtils.normalize(filePath);
		return list(new File(filePath), orderType);
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
	protected String uploadInternal(String group, KindDirType dir, MultipartMessage item) throws IOException {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotEmpty(group)) {
			sb.append(group).append("/");
		}
		sb.append(dir).append("/");
		sb.append(TimeUtils.format(System.currentTimeMillis(), "yyyy/MM/dd"));
		sb.append("/");
		sb.append(item.getOriginalFilename());
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
		FileUtils.copyInputStreamToFile(item.getInputStream(), file);

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
