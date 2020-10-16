package scw.tencent.qq.connect;

import java.util.List;

import scw.json.JSONUtils;
import scw.json.JsonObject;

public class ListPhotoResponse extends QQResponse {

	public ListPhotoResponse(JsonObject target) {
		super(target);
	}

	public int getTotal() {
		return getIntValue("total");
	}

	public List<Photo> getPhotos() {
		return JSONUtils.wrapper(getJsonArray("photos"), Photo.class);
	}
}
