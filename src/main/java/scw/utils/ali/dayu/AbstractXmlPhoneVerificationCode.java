package scw.utils.ali.dayu;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import com.alibaba.fastjson.JSONObject;

import scw.beans.annotation.Destroy;
import scw.common.ProcessResult;
import scw.common.utils.StringUtils;
import scw.common.utils.XMLUtils;
import scw.common.utils.XTime;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public abstract class AbstractXmlPhoneVerificationCode implements XmlPhoneVerificationCode {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private final static String LAST_TIME_SEND_NAME = "lastSendTime";
	private final static String CODE_NAME = "code";
	private final static String COUNT_NAME = "count";

	private final String codeParameterKey;
	private final int codeLength;
	private final boolean debug;
	private final int everyDayMaxSize;// 每天发送限制
	private final int maxTimeInterval;// 两次发送时间限制
	private final int maxActiveTime;// 验证码有效时间
	private final List<MessageModel> modelList;
	private final AliDaYu aLiDaYu;

	public AbstractXmlPhoneVerificationCode(String xmlPath)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Node root = XMLUtils.getRootElement(xmlPath);
		String host = XMLUtils.getNodeAttributeValue(root, "host", "http://gw.api.taobao.com/router/rest");
		String appKey = XMLUtils.getRequireNodeAttributeValue(root, "appKey");
		String version = XMLUtils.getNodeAttributeValue(root, "version", "2.0");
		String format = XMLUtils.getNodeAttributeValue(root, "format", "json");
		String signMethod = XMLUtils.getNodeAttributeValue(root, "sign-method", "md5");
		String appSecret = XMLUtils.getRequireNodeAttributeValue(root, "appSecret");
		boolean async = XMLUtils.getBooleanValue(root, "async", false);

		if (async) {
			this.aLiDaYu = new AsyncAliDaYu(host, appKey, version, format, signMethod, appSecret);
		} else {
			this.aLiDaYu = new DefaultAliDaYu(host, appKey, version, format, signMethod, appSecret);
		}

		this.modelList = XMLUtils.getBeanList(root, MessageModel.class);
		this.codeParameterKey = XMLUtils.getNodeAttributeValue(String.class, root, "code-key", "code");
		this.codeLength = XMLUtils.getNodeAttributeValue(Integer.class, root, "code-length", 6);
		this.debug = XMLUtils.getNodeAttributeValue(boolean.class, root, "debug", false);
		this.everyDayMaxSize = XMLUtils.getNodeAttributeValue(Integer.class, root, "everyDayMaxSize", 10);
		this.maxTimeInterval = XMLUtils.getNodeAttributeValue(Integer.class, root, "maxTimeInterval", 30);
		this.maxActiveTime = XMLUtils.getNodeAttributeValue(Integer.class, root, "maxActiveTime", 300);

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

	public ProcessResult<String> sendMessage(int configIndex, final String sms_param, final String toPhones) {
		MessageModel messageModel = getMessageModel(configIndex);
		if (messageModel == null) {
			return ProcessResult.simpleError("系统配置错误");
		}

		return sendMessage(messageModel, sms_param, toPhones);
	}

	@Destroy
	public void destroy() {
		if (aLiDaYu instanceof AsyncAliDaYu) {
			((AsyncAliDaYu) aLiDaYu).destory();
		}
	}

	public ProcessResult<String> sendMessage(MessageModel messageModel, String sms_param, String toPhones) {
		return aLiDaYu.sendMessage(messageModel, sms_param, toPhones);
	}

	public String getNewCode() {
		return StringUtils.getNumCode(codeLength);
	}

	public ProcessResult<String> sendCode(int configIndex, String phone, Map<String, String> parameterMap) {
		ProcessResult<String> processResult = canSend(configIndex, phone);
		if (processResult.isError()) {
			return processResult;
		}

		if (parameterMap == null) {
			parameterMap = new HashMap<String, String>();
		}

		String code = getNewCode();
		parameterMap.put(codeParameterKey, code);
		if (debug) {
			logger.debug("向[{}]发送验证码:{}", phone, code);
		}

		processResult = sendMessage(configIndex, JSONObject.toJSONString(parameterMap), phone);
		if (processResult.isError()) {
			return processResult;
		}

		successCall(configIndex, phone, code);
		return processResult;
	}

	public String getCodeParameterKey() {
		return codeParameterKey;
	}

	public int getCodeLength() {
		return codeLength;
	}

	public boolean isDebug() {
		return debug;
	}

	public int getEveryDayMaxSize() {
		return everyDayMaxSize;
	}

	public int getMaxTimeInterval() {
		return maxTimeInterval;
	}

	public int getMaxActiveTime() {
		return maxActiveTime;
	}

	public boolean checkCode(int configIndex, String phone, String code, boolean duplicateCheck) {
		JSONObject json = getCacheData(configIndex, phone);
		if (json == null) {
			return false;
		}

		long lastSendTime = json.getLong(LAST_TIME_SEND_NAME);
		if (getMaxActiveTime() > 0 && (System.currentTimeMillis() - lastSendTime) > getMaxActiveTime() * 1000L) {
			return false;// 验证码已过期
		}

		String cacheCode = json.getString(CODE_NAME);
		if (!code.equals(cacheCode)) {
			return false;// 验证码错误
		}

		if (!duplicateCheck) {// 如果此验证码不可以重复验证，那么在验证后删除此验证码
			json.put(CODE_NAME, "");
			setCacheData(configIndex, phone, json);
		}
		return true;
	}

	public void successCall(int configIndex, String phone, String code) {
		JSONObject json = getCacheData(configIndex, phone);
		if (json == null) {
			json = new JSONObject();
		}

		if (System.currentTimeMillis() - json.getLongValue(LAST_TIME_SEND_NAME) > XTime.ONE_DAY) {
			json.put(COUNT_NAME, 1);
		} else {
			json.put(COUNT_NAME, json.getIntValue(COUNT_NAME) + 1);
		}

		json.put(CODE_NAME, code);
		json.put(LAST_TIME_SEND_NAME, System.currentTimeMillis());
		setCacheData(configIndex, phone, json);
	}

	public <T> ProcessResult<T> canSend(int configIndex, String phone) {
		JSONObject json = getCacheData(configIndex, phone);
		if (json == null) {
			return ProcessResult.success();
		}

		long lastSendTime = json.getLong(LAST_TIME_SEND_NAME);
		if (getMaxTimeInterval() > 0 && (System.currentTimeMillis() - lastSendTime) < getMaxTimeInterval() * 1000L) {
			return ProcessResult.simpleError("发送过于频繁");
		}

		int count = json.getIntValue(COUNT_NAME);
		if (getEveryDayMaxSize() > 0 && (count > getEveryDayMaxSize())) {
			return ProcessResult.simpleError("今日发送次数过多");
		}
		return ProcessResult.success();
	}

	public abstract JSONObject getCacheData(int configIndex, String phone);

	public abstract void setCacheData(int configIndex, String phone, JSONObject json);
}
