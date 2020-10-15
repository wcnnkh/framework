package scw.tencent.wx.pay;

/**
 * {@link https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=4_2}
 * @author shuchaowen
 *
 */
public enum TradeType {
	/**
	 * JSAPI支付（或小程序支付）
	 */
	JSAPI,
	/**
	 * Native支付
	 */
	NATIVE,
	/**
	 * app支付
	 */
	APP,
	/**
	 * H5支付
	 */
	MWEB,
	/**
	 * 付款码支付，付款码支付有单独的支付接口，所以接口不需要上传，该字段在对账单中会出现
	 */
	MICROPAY
}
