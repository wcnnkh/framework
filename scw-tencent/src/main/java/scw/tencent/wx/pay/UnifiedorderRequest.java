package scw.tencent.wx.pay;

import java.io.Serializable;

import scw.lang.Nullable;

/**
 * 统一下单 字段说明见：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1
 * 
 * @author shuchaowen
 *
 */
public class UnifiedorderRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private String device_info;
	private final String body;
	private String detail;
	private String attach;
	private final String out_trade_no;
	private String fee_type;
	private final int total_fee;
	private final String spbill_create_ip;
	private String time_start;
	private String time_expire;
	private String goods_tag;
	private final String notify_url;
	private final String trade_type;
	private String product_id;
	private String limit_pay;
	private String openid;

	/**
	 * @param body 商品描述交易字段格式根据不同的应用场景按照以下格式：APP——需传入应用市场上的APP名字-实际商品名称，天天爱消除-游戏充值。
	 * @param out_trade_no 	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*且在同一个商户号下唯一。
	 * @param total_fee 订单总金额，单位为分
	 * @param spbill_create_ip 支持IPV4和IPV6两种格式的IP地址。调用微信支付API的机器IP
	 * @param notify_url 接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
	 * @param trade_type 支付类型
	 */
	public UnifiedorderRequest(String body, String out_trade_no, int total_fee, String spbill_create_ip,
			@Nullable String notify_url, String trade_type) {
		this.body = body;
		this.out_trade_no = out_trade_no;
		this.total_fee = total_fee;
		this.spbill_create_ip = spbill_create_ip;
		this.notify_url = notify_url;
		this.trade_type = trade_type;
	}

	public String getDevice_info() {
		return device_info;
	}

	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}

	public String getBody() {
		return body;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public String getFee_type() {
		return fee_type;
	}

	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}

	public int getTotal_fee() {
		return total_fee;
	}

	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}

	public String getTime_start() {
		return time_start;
	}

	public void setTime_start(String time_start) {
		this.time_start = time_start;
	}

	public String getTime_expire() {
		return time_expire;
	}

	public void setTime_expire(String time_expire) {
		this.time_expire = time_expire;
	}

	public String getGoods_tag() {
		return goods_tag;
	}

	public void setGoods_tag(String goods_tag) {
		this.goods_tag = goods_tag;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getLimit_pay() {
		return limit_pay;
	}

	public void setLimit_pay(String limit_pay) {
		this.limit_pay = limit_pay;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public void useAssert() {
	}
}
