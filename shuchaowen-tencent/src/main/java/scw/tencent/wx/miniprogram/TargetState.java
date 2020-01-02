package scw.tencent.wx.miniprogram;

/**
 * 
 * 状态 文字内容 颜色 允许转移的状态 0 "成员正在加入，当前 {member_count}/{room_limit} 人" #FA9D39 0, 1 1
 * "已开始" #CCCCCC 无
 * 
 * 活动的默认有效期是 24 小时。活动结束后，消息内容会变成统一的样式： 文字内容：“已结束” 文字颜色：#00ff00
 * 
 * @author shuchaowen
 *
 */
public enum TargetState {
	NotYetBegun(0), AlreadyBegun(1),;

	private final int state;

	private TargetState(int state) {
		this.state = state;
	}

	public int getState() {
		return state;
	}
}
