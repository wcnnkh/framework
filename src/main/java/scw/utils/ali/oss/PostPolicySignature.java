package scw.utils.ali.oss;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

import scw.servlet.view.AbstractTextView;

public final class PostPolicySignature extends AbstractTextView implements Serializable{
	private static final long serialVersionUID = -7835802870759182746L;
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
	
	@Override
	public String getResponseText() {
		JSONObject json = new JSONObject();
		json.put("key", key);
		json.put("OSSAccessKeyId", accessId);
		json.put("policy", policy);
		json.put("Signature", signature);
		return json.toJSONString();
	}
}
