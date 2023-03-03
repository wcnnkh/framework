package io.basc.framework.upload.kind;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.http.MediaType;
import io.basc.framework.json.JsonUtils;
import io.basc.framework.net.MimeType;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.web.message.model.Text;

public class KindManagerResult implements Serializable, Text {
	private static final long serialVersionUID = 1L;
	private String moveup_dir_path;
	private String current_dir_path;
	private String current_url;
	private long total_count;
	private List<KindFileItem> file_list;

	public String getMoveup_dir_path() {
		return moveup_dir_path;
	}

	public void setMoveup_dir_path(String moveup_dir_path) {
		this.moveup_dir_path = moveup_dir_path;
	}

	public String getCurrent_dir_path() {
		return current_dir_path;
	}

	public void setCurrent_dir_path(String current_dir_path) {
		this.current_dir_path = current_dir_path;
	}

	public String getCurrent_url() {
		return current_url;
	}

	public void setCurrent_url(String current_url) {
		this.current_url = current_url;
	}

	public long getTotal_count() {
		return total_count;
	}

	public void setTotal_count(long total_count) {
		this.total_count = total_count;
	}

	public List<KindFileItem> getFile_list() {
		return file_list;
	}

	public void setFile_list(List<KindFileItem> file_list) {
		this.file_list = file_list;
	}

	public String toTextContent() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("moveup_dir_path", getMoveup_dir_path());
		map.put("current_dir_path", getCurrent_dir_path());
		map.put("current_url", getCurrent_url());
		map.put("total_count", getTotal_count());
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (!CollectionUtils.isEmpty(file_list)) {
			for (KindFileItem item : file_list) {
				list.add(item.toResponseResult());
			}
		}
		map.put("file_list", list);
		return JsonUtils.getSupport().toJsonString(map);
	}

	public MimeType getMimeType() {
		return MediaType.APPLICATION_JSON;
	}

	@Override
	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
