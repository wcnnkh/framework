package scw.upload.kind;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import scw.core.utils.XTime;
import scw.mapper.MapperUtils;

public class KindFileItem implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean dir;
	private boolean hasFile;
	private long size;
	private String dirPath;
	private boolean photo;
	private String type;
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

	public String getDirPath() {
		return dirPath;
	}

	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}

	public boolean isPhoto() {
		return photo;
	}

	public void setPhoto(boolean photo) {
		this.photo = photo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
		map.put("dir_path", getDirPath());
		map.put("is_photo", isPhoto());
		map.put("filetype", getType());
		map.put("name", getName());
		map.put("datetime", XTime.format(getDateTime(), "yyyy-MM-dd HH:mm:ss"));
		return map;
	}

	@Override
	public String toString() {
		return MapperUtils.getMapper().toString(this);
	}
}
