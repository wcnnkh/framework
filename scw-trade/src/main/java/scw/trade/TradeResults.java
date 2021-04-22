package scw.trade;

import scw.lang.Nullable;

public class TradeResults extends Trade {
	private static final long serialVersionUID = 1L;
	/**
	 * 第三方交易id
	 */
	@Nullable
	private String thirdpartyTradeNo;
	
	private boolean success;

	public String getThirdpartyTradeNo() {
		return thirdpartyTradeNo;
	}

	public void setThirdpartyTradeNo(String thirdpartyTradeNo) {
		this.thirdpartyTradeNo = thirdpartyTradeNo;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
