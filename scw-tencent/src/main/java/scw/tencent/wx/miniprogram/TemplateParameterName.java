package scw.tencent.wx.miniprogram;

public enum TemplateParameterName {
	/**
	 * target_state = 0 时必填，文字内容模板中 member_count 的值
	 */
	member_count,
	/**
	 * target_state = 0 时必填，文字内容模板中 room_limit 的值
	 */
	room_limit,
	/**
	 * target_state = 1 时必填，点击「进入」启动小程序时使用的路径。对于小游戏，没有页面的概念，可以用于传递查询字符串（query），如
	 * "?foo=bar"
	 */
	path,
	/**
	 * target_state = 1
	 * 时必填，点击「进入」启动小程序时使用的版本。有效参数值为：develop（开发版），trial（体验版），release（正式版）
	 */
	version_type,;
}
