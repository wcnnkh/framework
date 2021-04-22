package scw.alibaba.trade;

import java.util.Map;

import scw.alibaba.pay.TradeStatus;
import scw.context.annotation.Provider;
import scw.core.Ordered;
import scw.http.server.ServerHttpRequest;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mapper.Copy;
import scw.mvc.MVCUtils;
import scw.trade.TradeException;
import scw.trade.create.TradeCreate;
import scw.trade.create.TradeCreateAdapter;
import scw.trade.create.TradeCreateResponse;
import scw.trade.refund.TradeRefund;
import scw.trade.refund.TradeRefundAdapter;
import scw.trade.status.TradeResultsEvent;
import scw.trade.status.TradeStatusDispatcher;
import scw.trade.web.TradeNotifyAdapter;
import scw.trade.web.TradeNotifyConfig;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class AlipayTradeAdapter implements TradeCreateAdapter,
		TradeRefundAdapter, TradeNotifyAdapter {
	private static Logger logger = LoggerFactory
			.getLogger(AlipayTradeAdapter.class);
	/**
	 * app支付
	 */
	public static final String APP_METHOD = "alipay_app";

	private static final String SUCCESS_TEXT = "SUCCESS";

	private final TradeNotifyConfig notifyConfig;
	private final AlipayConfig alipayConfig;
	private final AlipayClient alipayClient;

	public AlipayTradeAdapter(TradeNotifyConfig notifyConfig,
			AlipayConfig alipayConfig) {
		this.notifyConfig = notifyConfig;
		this.alipayConfig = alipayConfig;
		this.alipayClient = alipayConfig.isVerified() ? new DefaultAlipayClient(
				"https://openapi.alipay.com", alipayConfig.getAppId(),
				alipayConfig.getPrivateKey(), alipayConfig.getDataType(),
				alipayConfig.getCharset(), alipayConfig.getPublicKey(),
				alipayConfig.getSignType()) : null;
	}

	@Override
	public boolean isAccept(String method) {
		return APP_METHOD.equals(method) && alipayConfig != null;
	}

	@Override
	public Object notify(String tradeMethod, String tradeStatus, ServerHttpRequest request,
			TradeStatusDispatcher dispatcher) throws TradeException {
		logger.info("收到支付宝回调-----");
		Map<String, String> params = MVCUtils
				.getRequestParameterAndAppendValues(request, ",");
		// 切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
		// boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String
		// publicKey, String charset, String sign_type)
		logger.info(JSONUtils.getJsonSupport().toJSONString(params));

		boolean flag;
		try {
			flag = AlipaySignature.rsaCheckV1(params,
					alipayConfig.getPublicKey(), alipayConfig.getCharset(),
					alipayConfig.getSignType());
		} catch (AlipayApiException e) {
			logger.error(e, e.getMessage());
			return "check sign error";
		}

		if (!flag) {
			logger.error("支付验证签名错误");
			return "sign error";
		}

		String out_trade_no = params.get("out_trade_no");
		int amount = Integer.parseInt(params.get("total_amount"));
		String tradeNo = params.get("trade_no");
		TradeStatus status = TradeStatus.forName(params.get("trade_status"));
		if (TradeStatus.TRADE_SUCCESS == status) {
			TradeResultsEvent event = new TradeResultsEvent();
			event.setExtended(params);

			event.setTradeAmount(amount);
			event.setSubject(params.get("subject"));
			event.setThirdpartyTradeNo(tradeNo);
			event.setTradeNo(out_trade_no);
			event.setCreateTime(System.currentTimeMillis());
			event.setTradeMethod(APP_METHOD);
			event.setSuccess(true);
			dispatcher.publishEvent(tradeStatus, event);
		}
		return SUCCESS_TEXT;
	}

	@Override
	public boolean refund(TradeRefund request) throws TradeException {
		AlipayTradeRefundModel model = new AlipayTradeRefundModel();
		model.setOutTradeNo(request.getTradeNo());
		model.setRefundAmount(request.getTradeAmountDescribe());
		model.setRefundReason(request.getSubject());
		model.setTradeNo(request.getThirdpartyTradeNo());
		AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
		alipayRequest.setBizModel(model);
		alipayRequest.setNotifyUrl(notifyConfig.getNotifyUrl(request
				.getTradeMethod(), scw.trade.TradeStatus.REFUND));
		try {
			AlipayTradeRefundResponse response = alipayClient
					.sdkExecute(alipayRequest);
			if (response.isSuccess()) {
				return true;
			}

			logger.error("退款失败：{}", response);
			return false;
		} catch (AlipayApiException e) {
			throw new TradeException(e);
		}
	}

	@Override
	public TradeCreateResponse create(TradeCreate tradeCreate)
			throws TradeException {
		AlipayTradeAppPayRequest alipayRequest = new AlipayTradeAppPayRequest();
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		model.setSubject(tradeCreate.getSubject());
		model.setOutTradeNo(tradeCreate.getTradeNo());
		model.setTimeoutExpress("30m");
		model.setTotalAmount(tradeCreate.getTradeAmountDescribe());
		model.setProductCode("QUICK_MSECURITY_PAY");
		alipayRequest.setBizModel(model);
		alipayRequest.setNotifyUrl(notifyConfig.getNotifyUrl(tradeCreate
				.getTradeMethod(), scw.trade.TradeStatus.SUCCESS));
		try {
			AlipayTradeAppPayResponse response = alipayClient
					.sdkExecute(alipayRequest);

			TradeCreateResponse paymentResponse = new TradeCreateResponse();
			Copy.copy(paymentResponse, response);
			paymentResponse.setCredential(response.getBody());
			return paymentResponse;
		} catch (AlipayApiException e) {
			throw new TradeException(e);
		}
	}

}
