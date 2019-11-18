package scw.result.support;

import scw.result.BaseResult;

public class DefaultBaseResult implements BaseResult{
	private boolean success;
	private String msg;
	
	@SuppressWarnings("unused")
	private DefaultBaseResult(){};
	
	public DefaultBaseResult(boolean success, String msg){
		this.success = success;
		this.msg = msg;
	}
	
	
	public boolean isSuccess() {
		return success;
	}

	public String getMsg() {
		return msg;
	}

}
