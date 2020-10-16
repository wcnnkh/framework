package scw.tencent.qq.connect;

import java.util.List;

import scw.json.JsonObject;

public class ListAlbumResponse extends QQResponse {

	public ListAlbumResponse(JsonObject target) {
		super(target);
	}

	/**
	 * albumnum
	 * 
	 * @return
	 */
	public int getAlbumnum() {
		return getIntValue("albumnum");
	}

	public List<AlbumInfo> getAlbums() {
		return AlbumInfo.parse(getJsonArray("album"));
	}
}
