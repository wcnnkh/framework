package shuchaowen.web.servlet.parameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONObject;

import shuchaowen.common.utils.IOUtils;
import shuchaowen.common.utils.XUtils;
import shuchaowen.web.servlet.RequestWrapper;
import shuchaowen.web.servlet.Request;

public class RequestFormat extends RequestWrapper {
	public RequestFormat(Request request) {
		super(request);
	}

	public String getBody() {
		BufferedReader br = null;
		String content = null;
		try {
			br = getRequest().getReader();
			if (br.markSupported()) {
				br.mark(0);
			}
			content = IOUtils.readerContent(br).toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br.markSupported()) {
				if (br != null) {
					try {
						br.reset();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			XUtils.close(br);
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
