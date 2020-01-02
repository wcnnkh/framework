package scw.integration.apple;

import java.util.List;

public class Receipt {
	private String receipt_type;
	private int adam_id;
	private int app_item_id;
	private String bundle_id;
	private String application_version;
	private int download_id;
	private int version_external_identifier;
	private String receipt_creation_data;
	private long receipt_creation_data_ms;
	private String receipt_creation_data_pst;
	private String request_data;
	private long request_data_ms;
	private String request_data_pst;
	private String original_purchase_data;
	private long original_purchase_data_ms;
	private String original_purchase_dat_pst;
	private String origin_application_version;
	private List<App> in_app;

	public String getReceipt_type() {
		return receipt_type;
	}

	public void setReceipt_type(String receipt_type) {
		this.receipt_type = receipt_type;
	}

	public int getAdam_id() {
		return adam_id;
	}

	public void setAdam_id(int adam_id) {
		this.adam_id = adam_id;
	}

	public int getApp_item_id() {
		return app_item_id;
	}

	public void setApp_item_id(int app_item_id) {
		this.app_item_id = app_item_id;
	}

	public String getBundle_id() {
		return bundle_id;
	}

	public void setBundle_id(String bundle_id) {
		this.bundle_id = bundle_id;
	}

	public String getApplication_version() {
		return application_version;
	}

	public void setApplication_version(String application_version) {
		this.application_version = application_version;
	}

	public int getDownload_id() {
		return download_id;
	}

	public void setDownload_id(int download_id) {
		this.download_id = download_id;
	}

	public int getVersion_external_identifier() {
		return version_external_identifier;
	}

	public void setVersion_external_identifier(int version_external_identifier) {
		this.version_external_identifier = version_external_identifier;
	}

	public String getReceipt_creation_data() {
		return receipt_creation_data;
	}

	public void setReceipt_creation_data(String receipt_creation_data) {
		this.receipt_creation_data = receipt_creation_data;
	}

	public long getReceipt_creation_data_ms() {
		return receipt_creation_data_ms;
	}

	public void setReceipt_creation_data_ms(long receipt_creation_data_ms) {
		this.receipt_creation_data_ms = receipt_creation_data_ms;
	}

	public String getReceipt_creation_data_pst() {
		return receipt_creation_data_pst;
	}

	public void setReceipt_creation_data_pst(String receipt_creation_data_pst) {
		this.receipt_creation_data_pst = receipt_creation_data_pst;
	}

	public String getRequest_data() {
		return request_data;
	}

	public void setRequest_data(String request_data) {
		this.request_data = request_data;
	}

	public long getRequest_data_ms() {
		return request_data_ms;
	}

	public void setRequest_data_ms(long request_data_ms) {
		this.request_data_ms = request_data_ms;
	}

	public String getRequest_data_pst() {
		return request_data_pst;
	}

	public void setRequest_data_pst(String request_data_pst) {
		this.request_data_pst = request_data_pst;
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

	public String getOriginal_purchase_dat_pst() {
		return original_purchase_dat_pst;
	}

	public void setOriginal_purchase_dat_pst(String original_purchase_dat_pst) {
		this.original_purchase_dat_pst = original_purchase_dat_pst;
	}

	public String getOrigin_application_version() {
		return origin_application_version;
	}

	public void setOrigin_application_version(String origin_application_version) {
		this.origin_application_version = origin_application_version;
	}

	public List<App> getIn_app() {
		return in_app;
	}

	public void setIn_app(List<App> in_app) {
		this.in_app = in_app;
	}
}
