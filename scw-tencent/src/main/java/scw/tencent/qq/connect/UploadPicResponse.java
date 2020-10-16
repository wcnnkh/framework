package scw.tencent.qq.connect;

import scw.json.JsonObject;

public class UploadPicResponse extends QQResponse {

	public UploadPicResponse(JsonObject target) {
		super(target);
	}

	public String getAlbumid() {
		return getString("albumid");
	}

	public String getLloc() {
		return getString("lloc");
	}

	public String getSloc() {
		return getString("sloc");
	}

	public String getLargeUrl() {
		return getString("large_url");
	}

	public String getSmallUrl() {
		return getString("small_url");
	}

	public int getHeight() {
		return getIntValue("height");
	}

	public int getWidth() {
		return getIntValue("width");
	}
}
