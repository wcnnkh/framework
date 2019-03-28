package scw.beans.tcc;

/**
 * 步骤类型
 * 
 * @author shuchaowen
 *
 */
public enum StageType {
	Try(0), Confirm(1), Cancel(2), Complete(3);

	private final int status;

	StageType(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
