/*
 * Created on 27.03.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.db;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Product extends Table {

	private Long productId = new Long(0l);
	
	private String name = "";
	
	private String description = "";
	
	private Integer minimalQuantity = new Integer(0);
	
	private Integer maximalQuantity = new Integer(1);
	
	private Integer criticalQuantity = new Integer(0);
	
	private Double currentPrice = new Double(0d);
	
	private ProductGroup productGroup = ProductGroup.getDefaultGroup();
	
	private String eanCode = "";
	
	private String productCode = "";

	/**
	 * 
	 */
	public Product() {
		super();
	}

	/* (non-Javadoc)
	 * @see ch.eugster.pos.db.Table#isRemovable()
	 */
	public boolean isRemovable() {
		return true;
	}

	/**
	 * @return
	 */
	public Integer getCriticalQuantity() {
		return criticalQuantity;
	}

	/**
	 * @return
	 */
	public Double getCurrentPrice() {
		return currentPrice;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return
	 */
	public String getEanCode() {
		return eanCode;
	}

	/**
	 * @return
	 */
	public Integer getMaximalQuantity() {
		return maximalQuantity;
	}

	/**
	 * @return
	 */
	public Integer getMinimalQuantity() {
		return minimalQuantity;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * @return
	 */
	public ProductGroup getProductGroup() {
		return productGroup;
	}

	/**
	 * @return
	 */
	public Long getProductId() {
		return productId;
	}

	/**
	 * @param integer
	 */
	public void setCriticalQuantity(Integer integer) {
		criticalQuantity = integer;
	}

	/**
	 * @param double1
	 */
	public void setCurrentPrice(Double double1) {
		currentPrice = double1;
	}

	/**
	 * @param string
	 */
	public void setDescription(String string) {
		description = string;
	}

	/**
	 * @param string
	 */
	public void setEanCode(String string) {
		eanCode = string;
	}

	/**
	 * @param integer
	 */
	public void setMaximalQuantity(Integer integer) {
		maximalQuantity = integer;
	}

	/**
	 * @param integer
	 */
	public void setMinimalQuantity(Integer integer) {
		minimalQuantity = integer;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param string
	 */
	public void setProductCode(String string) {
		productCode = string;
	}

	/**
	 * @param group
	 */
	public void setProductGroup(ProductGroup group) {
		productGroup = group;
	}

}
