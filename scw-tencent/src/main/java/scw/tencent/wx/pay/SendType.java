package scw.tencent.wx.pay;

public enum SendType {
	/**
	 * 通过API接口发放
	 */
	API,
	/**通过上传文件方式发放
	 * 
	 */
	UPLOAD,
	/**
	 * 通过活动方式发放
	 */
	ACTIVITY
}
