package scw.utils.apple.response;

public class App {
	private String quantity;
	private String product_id;
	private String transaction_id;
	private String original_transaction_id;
	private String purchase_data;
	private long purchase_ms;
	private String purchase_data_pst;
	private String original_purchase_data;
	private long original_purchase_data_ms;
	private String original_purchase_data_pst;
	private String is_trial_period;

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getOriginal_transaction_id() {
		return original_transaction_id;
	}

	public void setOriginal_transaction_id(String original_transaction_id) {
		this.original_transaction_id = original_transaction_id;
	}

	public String getPurchase_data() {
		return purchase_data;
	}

	public void setPurchase_data(String purchase_data) {
		this.purchase_data = purchase_data;
	}

	public long getPurchase_ms() {
		return purchase_ms;
	}

	public void setPurchase_ms(long purchase_ms) {
		this.purchase_ms = purchase_ms;
	}

	public String getPurchase_data_pst() {
		return purchase_data_pst;
	}

	public void setPurchase_data_pst(String purchase_data_pst) {
		this.purchase_data_pst = purchase_data_pst;
	}

	public String getOriginal_purchase_data() {
		return original_purchase_data;
	}

	public void setOriginal_purchase_data(String original_purchase_data) {
		this.original_purchase_data = original_purchase_data;
	}

	public long getOriginal_purchase_data_ms() {
		return original_purchase_data_ms;
	}

	public void setOriginal_purchase_data_ms(long original_purchase_data_ms) {
		this.original_purchase_data_ms = original_purchase_data_ms;
	}

	public String getOriginal_purchase_data_pst() {
		return original_purchase_data_pst;
	}

	public void setOriginal_purchase_data_pst(String original_purchase_data_pst) {
		this.original_purchase_data_pst = original_purchase_data_pst;
	}

	public String getIs_trial_period() {
		return is_trial_period;
	}

	public void setIs_trial_period(String is_trial_period) {
		this.is_trial_period = is_trial_period;
	}
}
