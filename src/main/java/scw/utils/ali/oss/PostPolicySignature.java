package scw.utils.ali.oss;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import scw.json.JSONUtils;
import scw.servlet.http.view.AbstractTextView;

/**
 * 请注意
 * 由于历史遗留问题，导致字段和返回的json字符串名称不一致
 * @author shuchaowen
 *
 */
public final class PostPolicySignature extends AbstractTextView implements Serializable {
	private static final long serialVersionUID = 1L;
	private String accessId;
	private String policy;
	private String signature;
	private String key;

	public String getAccessId() {
		return accessId;
	}

	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String convertToString() {
		Map<String, Object> map = new HashMap<String, Object>(4, 1);
		map.put("key", key);
		map.put("OSSAccessKeyId", accessId);
		map.put("policy", policy);
		map.put("Signature", signature);
		return JSONUtils.toJSONString(map);
	}
}
