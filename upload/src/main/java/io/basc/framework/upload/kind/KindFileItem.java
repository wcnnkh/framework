package io.basc.framework.upload.kind;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.TimeUtils;

public class KindFileItem implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean dir;
	private boolean hasFile;
	private long size;
	private boolean photo;
	private String ext;
	private String name;
	private long dateTime;

	public boolean isDir() {
		return dir;
	}

	public void setDir(boolean dir) {
		this.dir = dir;
	}

	public boolean isHasFile() {
		return hasFile;
	}

	public void setHasFile(boolean hasFile) {
		this.hasFile = hasFile;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean isPhoto() {
		return photo;
	}

	public void setPhoto(boolean photo) {
		this.photo = photo;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}

	public Map<String, Object> toResponseResult() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("is_dir", isDir());
		map.put("has_file", hasFile);
		map.put("filesize", getSize());
		map.put("is_photo", isPhoto());
		map.put("filetype", getExt());
		map.put("filename", getName());
		map.put("datetime", TimeUtils.format(getDateTime(), "yyyy-MM-dd HH:mm:ss"));
		return map;
	}

	@Override
	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
