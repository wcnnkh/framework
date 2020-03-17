package scw.tencent.wx.offiaccount;

public enum MsgType {
	TEXT("文本"),
	IMAGE("图片"),
	VOICE("语音"),
	VIDEO("视频"),
	SHORTVIDEO("小视频"),
	LOCATION("地理位置"),
	LINK("链接")
	;
	
	private final String describe;
	MsgType(String describe){
		this.describe = describe;
	}
	public String getDescribe() {
		return describe;
	}
}
