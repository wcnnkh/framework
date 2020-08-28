package scw.kdniao;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.util.ToMap;

public class Commodity implements Serializable, ToMap<String, Object> {
	private static final long serialVersionUID = 1L;
	// 商品名称
	private String goodsName;
	// 商品编码
	private String goodsCode;
	// 件数
	private Integer goodsQuantity;
	// 商品价格
	private Double goodsPrice;
	// 商品重量kg
	private Double goodsWeight;
	// 商品描述
	private String goodsDesc;
	// 商品体积m3
	private Double goodsVol;

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsCode() {
		return goodsCode;
	}

	public void setGoodsCode(String goodsCode) {
		this.goodsCode = goodsCode;
	}

	public Integer getGoodsQuantity() {
		return goodsQuantity;
	}

	public void setGoodsQuantity(Integer goodsQuantity) {
		this.goodsQuantity = goodsQuantity;
	}

	public Double getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(Double goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

	public Double getGoodsWeight() {
		return goodsWeight;
	}

	public void setGoodsWeight(Double goodsWeight) {
		this.goodsWeight = goodsWeight;
	}

	public String getGoodsDesc() {
		return goodsDesc;
	}

	public void setGoodsDesc(String goodsDesc) {
		this.goodsDesc = goodsDesc;
	}

	public Double getGoodsVol() {
		return goodsVol;
	}

	public void setGoodsVol(Double goodsVol) {
		this.goodsVol = goodsVol;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("GoodsName", goodsName);
		map.put("GoodsCode", goodsCode);
		map.put("Goodsquantity", goodsQuantity);
		map.put("GoodsPrice", goodsPrice);
		map.put("GoodsWeight", goodsWeight);
		map.put("GoodsDesc", goodsDesc);
		map.put("GoodsVol", goodsVol);
		return map;
	}
}
