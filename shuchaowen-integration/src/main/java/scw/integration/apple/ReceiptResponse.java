package scw.integration.apple;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public final class ReceiptResponse {
	private Integer status;
	private String receipt;
	private boolean sandbox;//是否是测试模式

	protected ReceiptResponse() {
	};
	
	protected void setSandbox(boolean sandbox){
		this.sandbox = sandbox;
	}

	public boolean isSandbox() {
		return sandbox;
	}

	public String getReceipt() {
		return receipt;
	}

	public Integer getStatus() {
		return status;
	}

	public boolean isSuccess() {
		return status != null && status == 0;
	}

	public boolean isError() {
		return status == null || status != 0;
	}

	/**
	 * 运行环境错误 ，应该到沙盒模式或正式模式下尝试
	 * 
	 * @return
	 */
	public boolean isOperationModeError() {
		return status == null || status == 21007 || status == 21003;
	}

	public String getProductId() {
		if (isError()) {
			return null;
		}

		JSONObject json = JSONObject.parseObject(receipt);
		if (json.containsKey("in_app")) {// ios7以上
			JSONArray in_app = json.getJSONArray("in_app");
			if (in_app == null || in_app.size() == 0) {
				return null;
			}

			json = in_app.getJSONObject(0);
			if (json == null) {
				return null;
			}

			return json.getString("product_id");
		} else {
			return json.getString("product_id");
		}
	}
}
