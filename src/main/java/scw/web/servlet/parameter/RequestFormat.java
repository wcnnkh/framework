package scw.web.servlet.parameter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONObject;

import scw.common.io.decoder.StringDecoder;
import scw.web.servlet.Request;

public final class RequestFormat {
	private Request request;

	public RequestFormat(Request request) {
		this.request = request;
	}

	/**
	 * @return the request
	 */
	public Request getRequest() {
		return request;
	}

	public String getBody() {
		String content = null;
		try {
			content = new StringDecoder(Charset.forName(getRequest().getCharacterEncoding()))
					.decode(getRequest().getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public JSONObject toJson() {
		return JSONObject.parseObject(getBody());
	}

	public Map<String, String> xmlToMap() {
		Map<String, String> map = new HashMap<String, String>();
		Document document = null;
		SAXReader reader = new SAXReader();
		try {
			document = reader.read(getRequest().getInputStream());
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Element root = document.getRootElement();
		int size = root.nodeCount();
		for (int i = 0; i < size; i++) {
			Node node = root.node(i);
			String k = node.getName();
			if (k == null) {
				continue;
			}
			map.put(k, node.getText());
		}
		return map;
	}
}
