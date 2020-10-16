package scw.tencent.qq.connect;

import scw.json.JsonObject;

public class AddAlbumResponse extends QQResponse{

	public AddAlbumResponse(JsonObject target) {
		super(target);
	}
	
	public AlbumInfo getAlbumInfo(){
		return new AlbumInfo(getJsonObject("album"));
	}
}
