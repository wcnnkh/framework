package scw.ali.dayu;

import org.w3c.dom.Node;

import scw.common.utils.XMLUtils;

public class XmlAliDaYu{
	private static final String HOST_NAME = "host";
	private static final String APPKEY_NAME = "appKey";
	private static final String VERSION_NAME = "version";
	private static final String FORMAT_NAME = "format";
	private static final String SIGN_METHOD_NAME = "sign-method";
	private static final String APPSECRET_NAME = "appSecret";
	
	private ALiDaYu aLiDaYu;
	
	public XmlAliDaYu(String xmlPath){
		Node root = XMLUtils.getRootNode(xmlPath);
		String host = XMLUtils.getNodeAttributeValue(root, HOST_NAME, "http://gw.api.taobao.com/router/rest");
	}
}
