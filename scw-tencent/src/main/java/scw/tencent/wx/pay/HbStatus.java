package scw.tencent.wx.pay;

/**
 * 红包状态
 * @author shuchaowen
 *
 */
public enum HbStatus {
	/**
	 * 发放中
	 */
	SENDING,
	/**
	 * 已发放待领取
	 */
	SENT,
	/**
	 * 发放失败
	 */
	FAILED,
	/**
	 * 已领取
	 */
	RECEIVED,
	/**
	 * 退款中
	 */
	RFUND_ING,
	/**
	 * 已退款
	 */
	REFUND
}
