package scw.upload.kind;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import scw.core.Assert;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.io.FileUtils;
import scw.upload.UploadException;
import scw.upload.UploadItem;

public abstract class AbstractKindUpload implements KindEditor {
	private final EnumMap<KindDirType, Set<String>> extMap = new EnumMap<KindDirType, Set<String>>(KindDirType.class);
	private final EnumMap<KindDirType, Long> maxSizeMap = new EnumMap<KindDirType, Long>(KindDirType.class);

	{
		setExts(KindDirType.image, "gif", "jpg", "jpeg", "png", "bmp");
		setExts(KindDirType.flash, "swf", "flv");
		setExts(KindDirType.media, "swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg", "asf", "rm", "rmvb");
		setExts(KindDirType.file, "doc", "docx", "xls", "xlsx", "ppt", "htm", "html", "txt", "zip", "rar", "gz", "bz2");
	}

	public void setExts(KindDirType dir, Collection<String> exts) {
		Set<String> set;
		if (CollectionUtils.isEmpty(exts)) {
			set = Collections.emptySet();
		} else {
			set = new HashSet<String>(exts);
		}
		extMap.put(dir, set);
	}

	public boolean isImage(String fileName) {
		String ext = StringUtils.getFilenameExtension(fileName);
		if (ext == null) {
			return false;
		}

		Set<String> exts = extMap.get(KindDirType.image);
		return exts != null && exts.contains(ext);
	}

	public void setExts(KindDirType dir, String... exts) {
		setExts(dir, Arrays.asList(exts));
	}

	public void setMaxSize(KindDirType dir, Long maxSize) {
		maxSizeMap.put(dir, maxSize);
	}

	public String upload(String group, KindDirType dir, UploadItem item) throws UploadException, IOException {
		Assert.requiredArgument(dir != null, "dir");
		Assert.requiredArgument(item != null, "item");

		String ext = StringUtils.getFilenameExtension(item.getName());
		if (ext == null) {
			throw new UploadException("无法获取文件后缀名(" + item.getName() + ")");
		}

		Set<String> exts = extMap.get(dir);
		if (!CollectionUtils.isEmpty(exts) && !exts.contains(ext)) {
			throw new UploadException("允许使用的文件后缀名：(" + exts + ")");
		}

		Long maxSize = maxSizeMap.get(dir);
		if (maxSize != null && maxSize > 0 && item.size() > maxSize) {
			throw new UploadException("允许上传的文件大小为(" + FileUtils.byteCountToDisplaySize(maxSize) + ")");
		}
		return uploadInternal(group, dir, item);
	}

	protected abstract String uploadInternal(String group, KindDirType dir, UploadItem item)
			throws IOException, UploadException;
}
