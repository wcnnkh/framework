package scw.tencent.qq.connect;

public class AddAlbumRequest extends QQRequest {
	private static final long serialVersionUID = 1L;
	private final String albumname;
	private String albumdesc;
	private AlbumPriv priv;

	public AddAlbumRequest(String accessToken, String openid, String albumname) {
		super(accessToken, openid);
		this.albumname = albumname;
	}

	public String getAlbumname() {
		return albumname;
	}

	public String getAlbumdesc() {
		return albumdesc;
	}

	public void setAlbumdesc(String albumdesc) {
		this.albumdesc = albumdesc;
	}

	public AlbumPriv getPriv() {
		return priv;
	}

	public void setPriv(AlbumPriv priv) {
		this.priv = priv;
	}
}
