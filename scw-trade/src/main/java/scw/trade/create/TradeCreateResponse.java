package scw.trade.create;

public class TradeCreateResponse extends TradeCreate {
	private static final long serialVersionUID = 1L;
	private Object credential;

	public Object getCredential() {
		return credential;
	}

	public void setCredential(Object credential) {
		this.credential = credential;
	}
}
