package scw.integration.tencent.wx.pay;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.json.JSONUtils;
import scw.lang.Nullable;
import scw.orm.ORMUtils;
import scw.util.ToMap;

@Nullable(false)
public final class UnifiedOrderRequest implements Serializable,
		ToMap<String, String> {
	private static final long serialVersionUID = 1L;
	private String appId;// 微信支付分配的公众账号ID（企业号corpid即为此appId）
	private String mchId;// 商户号
	@Nullable
	private String deviceInfo;// 设备号
	private String nonceStr;// 随机字符串
	@Nullable
	private String signType;// 签名类型
	private String body;// 商品描述
	@Nullable
	private String detail;// 商品详情
	@Nullable
	private String attach;// 附加数据
	private String outTradeNo;// 商户订单号
	@Nullable
	private String feeType;// 标价币种
	private String totalFee;// 标价金额
	private String spbillCreateIp;// 终端IP
	@Nullable
	private String timeStart;// 交易起始时间
	@Nullable
	private String timeExpire;// 交易结束时间
	@Nullable
	private String goodsTag;// 订单优惠标记
	private String notifyUrl;// 通知地址
	private String tradeType;// 交易类型
	@Nullable
	private String productId;
	@Nullable
	private String limitPay;
	@Nullable
	private String openId;
	@Nullable
	private String receipt;
	@Nullable
	private SceneInfo sceneInfo;

	public Map<String, String> toMap() {
		ORMUtils.getObjectOperations().verify(this);
		Map<String, String> map = new HashMap<String, String>();
		map.put("appid", appId);
		map.put("mch_id", mchId);
		if (StringUtils.hasText(deviceInfo)) {
			map.put("device_info", deviceInfo);
		}

		map.put("nonce_str", nonceStr);
		if (StringUtils.hasText(signType)) {
			map.put("sign_type", signType);
		}

		map.put("body", body);

		if (StringUtils.hasText(detail)) {
			map.put("detail", detail);
		}

		if (StringUtils.hasText(attach)) {
			map.put("attach", attach);
		}

		map.put("out_trade_no", outTradeNo);

		if (StringUtils.hasText(feeType)) {
			map.put("fee_type", feeType);
		}

		map.put("total_fee", totalFee);
		map.put("spbill_create_ip", spbillCreateIp);

		if (StringUtils.hasText(timeStart)) {
			map.put("time_start", timeStart);
		}

		if (StringUtils.hasText(timeExpire)) {
			map.put("time_expire", timeExpire);
		}

		if (StringUtils.hasText(goodsTag)) {
			map.put("goods_tag", goodsTag);
		}

		map.put("notify_url", notifyUrl);
		map.put("trade_type", tradeType);

		if (StringUtils.hasText(productId)) {
			map.put("product_id", productId);
		}

		if (StringUtils.hasText(limitPay)) {
			map.put("limit_pay", limitPay);
		}

		if (StringUtils.hasText(openId)) {
			map.put("openid", openId);
		}

		if (StringUtils.hasText(receipt)) {
			map.put("receipt", receipt);
		}

		if (sceneInfo != null) {
			Map<String, Object> sceneInfoMap = sceneInfo.toMap();
			if (!CollectionUtils.isEmpty(sceneInfoMap)) {
				map.put("scene_info", JSONUtils.toJSONString(sceneInfoMap));
			}
		}
		return map;
	}

	public static class SceneInfo implements Serializable,
			ToMap<String, Object> {
		private static final long serialVersionUID = 1L;
		private StoreInfo storeInfo;

		public Map<String, Object> toMap() {
			if (storeInfo == null) {
				return null;
			}

			Map<String, Object> sceneInfoMap = new HashMap<String, Object>();
			sceneInfoMap.put("store_info", storeInfo.toMap());
			return sceneInfoMap;
		}
	}

	@Nullable(false)
	public static class StoreInfo implements Serializable,
			ToMap<String, Object> {
		private static final long serialVersionUID = 1L;
		private String id;
		private String name;
		private String areaCode;
		private String address;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAreaCode() {
			return areaCode;
		}

		public void setAreaCode(String areaCode) {
			this.areaCode = areaCode;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public Map<String, Object> toMap() {
			Map<String, Object> map = new HashMap<String, Object>();
			if (StringUtils.hasText(id)) {
				map.put("id", id);
			}

			if (StringUtils.hasLength(name)) {
				map.put("name", name);
			}

			if (StringUtils.hasLength(areaCode)) {
				map.put("area_code", areaCode);
			}

			if (StringUtils.hasLength(address)) {
				map.put("address", address);
			}
			return map.isEmpty() ? null : map;
		}
	}
}
