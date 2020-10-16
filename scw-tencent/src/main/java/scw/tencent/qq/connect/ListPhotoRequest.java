package scw.tencent.qq.connect;

public class ListPhotoRequest extends QQRequest {
	private static final long serialVersionUID = 1L;
	private final String albumid;

	public ListPhotoRequest(String accessToken, String openid, String albumid) {
		super(accessToken, openid);
		this.albumid = albumid;
	}

	public String getAlbumid() {
		return albumid;
	}
}
