package scw.transaction.sql.jta;

import javax.transaction.xa.Xid;

public class MyXid implements Xid {
	private final byte[] branchQualifier;
	private final int formatId;
	private final byte[] globalTransactionId;

	public MyXid(byte[] branchQualifier, int formatId, byte[] globalTransactionId) {
		this.branchQualifier = branchQualifier;
		this.formatId = formatId;
		this.globalTransactionId = globalTransactionId;
	}

	public byte[] getBranchQualifier() {
		return branchQualifier;
	}

	public int getFormatId() {
		return formatId;
	}

	public byte[] getGlobalTransactionId() {
		return globalTransactionId;
	}

}
