package scw.alibaba.dayu;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import scw.context.result.Result;
import scw.context.result.ResultFactory;
import scw.convert.TypeDescriptor;
import scw.core.utils.XTime;
import scw.dom.DomUtils;
import scw.env.SystemEnvironment;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.RandomUtils;

public abstract class AbstractXmlPhoneVerificationCode implements XmlPhoneVerificationCode, scw.context.Destroy {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private final String codeParameterKey;
	private final int codeLength;
	private final boolean debug;
	private final int everyDayMaxSize;// 每天发送限制
	private final int maxTimeInterval;// 两次发送时间限制
	private int maxActiveTime;// 验证码有效时间
	private final List<MessageModel> modelList;
	private final AliDaYu aLiDaYu;
	private final ResultFactory resultFactory;

	@SuppressWarnings("unchecked")
	public AbstractXmlPhoneVerificationCode(String xmlPath, ResultFactory resultFactory)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this.resultFactory = resultFactory;
		Node root = DomUtils.getRootElement(SystemEnvironment.getInstance(), xmlPath);
		String host = DomUtils.getNodeAttributeValue(root, "host", "http://gw.api.taobao.com/router/rest");
		String appKey = DomUtils.getRequireNodeAttributeValue(root, "appKey");
		String version = DomUtils.getNodeAttributeValue(root, "version", "2.0");
		String format = DomUtils.getNodeAttributeValue(root, "format", "json");
		String signMethod = DomUtils.getNodeAttributeValue(root, "sign-method", "md5");
		String appSecret = DomUtils.getRequireNodeAttributeValue(root, "appSecret");
		boolean async = DomUtils.getBooleanValue(root, "async", false);
		if (async) {
			this.aLiDaYu = new AsyncAliDaYu(host, appKey, version, format, signMethod, appSecret, resultFactory);
		} else {
			this.aLiDaYu = new DefaultAliDaYu(host, appKey, version, format, signMethod, appSecret, resultFactory);
		}
		
		this.modelList = (List<MessageModel>) SystemEnvironment.getInstance().convert(root, TypeDescriptor.forObject(root), TypeDescriptor.collection(List.class, MessageModel.class));
		this.codeParameterKey = DomUtils.getNodeAttributeValue(String.class, root, "code-key", "code");
		this.codeLength = DomUtils.getNodeAttributeValue(Integer.class, root, "code-length", 6);
		this.debug = DomUtils.getNodeAttributeValue(boolean.class, root, "debug", false);
		this.everyDayMaxSize = DomUtils.getNodeAttributeValue(Integer.class, root, "everyDayMaxSize", 10);
		this.maxTimeInterval = DomUtils.getNodeAttributeValue(Integer.class, root, "maxTimeInterval", 30);
		this.maxActiveTime = DomUtils.getNodeAttributeValue(Integer.class, root, "maxActiveTime", 300);
		if (this.maxActiveTime <= 0) {
			maxActiveTime = 300;
		}
	}

	public MessageModel getMessageModel(int index) {
		if (modelList == null) {
			return null;
		}

		if (modelList.size() <= index) {
			return null;
		}

		return modelList.get(index);
	}

	public Result sendMessage(int configIndex, final String sms_param, final String toPhones) {
		MessageModel messageModel = getMessageModel(configIndex);
		if (messageModel == null) {
			return resultFactory.error("系统配置错误");
		}

		return sendMessage(messageModel, sms_param, toPhones);
	}

	public void destroy() {
		if (aLiDaYu instanceof AsyncAliDaYu) {
			((AsyncAliDaYu) aLiDaYu).destroy();
		}
	}

	public Result sendMessage(MessageModel messageModel, String sms_param, String toPhones) {
		return aLiDaYu.sendMessage(messageModel, sms_param, toPhones);
	}

	public String getNewCode() {
		return RandomUtils.getNumCode(codeLength);
	}

	public Result sendCode(int configIndex, String phone, Map<String, String> parameterMap) {
		Result processResult = canSend(configIndex, phone);
		if (!processResult.isSuccess()) {
			return processResult;
		}

		if (parameterMap == null) {
			parameterMap = new HashMap<String, String>();
		}

		String code = getNewCode();
		parameterMap.put(codeParameterKey, code);
		if (debug) {
			logger.info("向[{}]发送验证码:{}", phone, code);
		}

		processResult = sendMessage(configIndex, JSONUtils.getJsonSupport().toJSONString(parameterMap), phone);
		if (!processResult.isSuccess()) {
			return processResult;
		}

		successCall(configIndex, phone, code);
		return processResult;
	}

	public final String getCodeParameterKey() {
		return codeParameterKey;
	}

	public final int getCodeLength() {
		return codeLength;
	}

	public final boolean isDebug() {
		return debug;
	}

	public final int getEveryDayMaxSize() {
		return everyDayMaxSize;
	}

	public final int getMaxTimeInterval() {
		return maxTimeInterval;
	}

	public final int getMaxActiveTime() {
		return maxActiveTime;
	}

	public boolean checkCode(int configIndex, String phone, String code, boolean duplicateCheck) {
		PhoneVerificationCode json = getCacheData(configIndex, phone);
		if (json == null) {
			return false;
		}

		long lastSendTime = json.getLastSendTime();
		if (getMaxActiveTime() > 0 && (System.currentTimeMillis() - lastSendTime) > getMaxActiveTime() * 1000L) {
			return false;// 验证码已过期
		}

		String cacheCode = json.getCode();
		if (!code.equals(cacheCode)) {
			return false;// 验证码错误
		}

		if (!duplicateCheck) {// 如果此验证码不可以重复验证，那么在验证后删除此验证码
			json.setCode("");
			setCacheData(configIndex, phone, json);
		}
		return true;
	}

	public void successCall(int configIndex, String phone, String code) {
		PhoneVerificationCode json = getCacheData(configIndex, phone);
		if (json == null) {
			json = new PhoneVerificationCode();
		}

		if (System.currentTimeMillis() - json.getLastSendTime() > XTime.ONE_DAY) {
			json.setCount(1);
		} else {
			json.setCount(json.getCount() + 1);
		}

		json.setCode(code);
		json.setLastSendTime(System.currentTimeMillis());
		setCacheData(configIndex, phone, json);
	}

	public Result canSend(int configIndex, String phone) {
		PhoneVerificationCode info = getCacheData(configIndex, phone);
		if (info == null) {
			return resultFactory.success();
		}

		long lastSendTime = info.getLastSendTime();
		if (getMaxTimeInterval() > 0 && (System.currentTimeMillis() - lastSendTime) < getMaxTimeInterval() * 1000L) {
			return resultFactory.error("发送过于频繁");
		}

		int count = info.getCount();
		if (getEveryDayMaxSize() > 0 && (count > getEveryDayMaxSize())) {
			return resultFactory.error("今日发送次数过多");
		}
		return resultFactory.success();
	}

	public abstract PhoneVerificationCode getCacheData(int configIndex, String phone);

	public abstract void setCacheData(int configIndex, String phone, PhoneVerificationCode data);
}
