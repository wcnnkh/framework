package scw.ali.dayu;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.w3c.dom.Node;

import scw.common.utils.XMLUtils;

public class XmlAliDaYu {
	private static final String HOST_NAME = "host";
	private static final String APPKEY_NAME = "appKey";
	private static final String VERSION_NAME = "version";
	private static final String FORMAT_NAME = "format";
	private static final String SIGN_METHOD_NAME = "sign-method";
	private static final String APPSECRET_NAME = "appSecret";
	private List<MessageModel> modelList;

	private ALiDaYu aLiDaYu;

	public XmlAliDaYu(String xmlPath) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Node root = XMLUtils.getRootNode(xmlPath);
		String host = XMLUtils.getNodeAttributeValue(root, HOST_NAME,
				"http://gw.api.taobao.com/router/rest");
		String appKey = XMLUtils
				.getRequireNodeAttributeValue(root, APPKEY_NAME);
		String version = XMLUtils.getNodeAttributeValue(root, VERSION_NAME,
				"2.0");
		String format = XMLUtils.getNodeAttributeValue(root, FORMAT_NAME,
				"json");
		String signMethod = XMLUtils.getNodeAttributeValue(root,
				SIGN_METHOD_NAME, "md5");
		String appSecret = XMLUtils.getRequireNodeAttributeValue(root,
				APPSECRET_NAME);
		this.aLiDaYu = new ALiDaYu(host, appKey, version, format, signMethod,
				appSecret);
		this.modelList = XMLUtils.getBeanList(root.getChildNodes(),
				MessageModel.class);
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

	public String sendMessage(int configIndex, final String sms_param,
			final String toPhones) {
		MessageModel messageModel = getMessageModel(configIndex);
		if (messageModel == null) {
			return null;
		}

		return aLiDaYu.sendMessage(messageModel, sms_param, toPhones);
	}

	public void asyncSendMessage(int configIndex, String sms_param,
			String toPhones) {
		MessageModel messageModel = getMessageModel(configIndex);
		if (messageModel == null) {
			return;
		}

		aLiDaYu.asyncSendMessage(messageModel, sms_param, toPhones);
	}
	
	public void destroy() {
		aLiDaYu.destroy();
	}
}
