package nl.tudelft.simulation.supplychain.product;

import java.io.Serializable;
import java.util.Objects;

import org.djunits.value.vdouble.scalar.Mass;
import org.djunits.value.vdouble.scalar.Volume;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;

/**
 * Product represents a certain type of product. It might have a billOfMaterials
 * of other products and amounts, indicating from which products this product
 * can be put together in a production process. When a product does not have a
 * billOfMaterials, the product can not be manufactured in the simulation, in
 * other words, it is a raw material that has always to be acquired from a
 * supplier.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the
 * Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Product implements Serializable {
	/** the serial version uid. */
	private static final long serialVersionUID = 20221129L;

	/** the bill of materials (product, amount). */
	private BillOfMaterials billOfMaterials;

	/**
	 * the unit in which this product is produced, shipped (boxes, pallets, etc.).
	 */
	private final Sku sku;

	/** the descriptive name of the product. */
	private final String name;

	/** the average volume per unit. */
	private final Volume averageSkuVolume;

	/** the average weight per unit. */
	private final Mass averageSkuWeight;

	/** depreciation the depreciation as a fraction per day. */
	private double depreciation = 0.0;

	/** the current world market price of the product per unit. */
	private Money unitMarketPrice = new Money(0.0, MoneyUnit.USD);

	/**
	 * Construct a new product with an empty Bill of Materials.
	 * 
	 * @param model                  SupplyChainModelInterface; the model to
	 *                               register the products
	 * @param name                   String; the descriptive name of the product.
	 * @param sku                    Sku; the stock keeping unit in which this
	 *                               product is shipped (boxes, pallets, kilograms,
	 *                               m3, containers)
	 * @param initialUnitMarketPrice Money; the initial world market price of the
	 *                               product per SKU
	 * @param averageSkuWeight       Mass; the average weight per SKU
	 * @param averageSkuVolume       Volume; the average volume per SKU
	 * @param depreciation           double; the depreciation as a factor per day
	 *                               for the product
	 */
	public Product(final SupplyChainModelInterface model, final String name, final Sku sku,
			final Money initialUnitMarketPrice, final Mass averageSkuWeight, final Volume averageSkuVolume,
			final double depreciation) {
		Throw.whenNull(name, "name cannot be null");
		Throw.whenNull(sku, "sku cannot be null");
		Throw.whenNull(initialUnitMarketPrice, "initialUnitMarketPrice cannot be null");
		Throw.whenNull(averageSkuWeight, "averageSkuWeight cannot be null");
		Throw.whenNull(averageSkuVolume, "averageSkuVolume cannot be null");
		this.name = name;
		this.sku = sku;
		this.unitMarketPrice = initialUnitMarketPrice;
		this.billOfMaterials = new BillOfMaterials(this);
		this.averageSkuWeight = averageSkuWeight;
		this.averageSkuVolume = averageSkuVolume;
		this.depreciation = depreciation;
	}

	/**
	 * Method getBillOfMaterials. Returns the entire BOM as one Map.
	 * 
	 * @return Map
	 */
	public BillOfMaterials getBillOfMaterials() {
		return this.billOfMaterials;
	}

	/**
	 * Method setBillOfMaterials. Sets the new BOM as one Map. The copy of the BOM
	 * is a shallow copy, which only copies the pointer to the map, so changing a
	 * parameter within the provided billOfMaterials method may change the BOM of
	 * this product!
	 * 
	 * @param billOfMaterials BillOfMaterials; the new Bill of Materials
	 */
	public void setBillOfMaterials(final BillOfMaterials billOfMaterials) {
		Throw.whenNull(billOfMaterials, "billOfMaterials cannot be null");
		this.billOfMaterials = billOfMaterials;
	}

	/**
	 * Return the stock keeping unit of this product.
	 * 
	 * @return SKU; the stock keeping unit of this product
	 */
	public Sku getSku() {
		return this.sku;
	}

	/**
	 * Return the product name.
	 * 
	 * @return String; the product name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the average sku weight as a Mass.
	 * 
	 * @return Mass; the averag weight per SKU
	 */
	public Mass getAverageSkuWeight() {
		return this.averageSkuWeight;
	}

	/**
	 * Return the average sku volume as a Volume.
	 * 
	 * @return Volume; the averag volume per SKU
	 */
	public Volume getAverageSkuVolume() {
		return this.averageSkuVolume;
	}

	/**
	 * Return the depreciation fraction.
	 * 
	 * @return double; the depreciation fraction
	 */
	public double getDepreciation() {
		return this.depreciation;
	}

	/**
	 * Return the current average unit market price per SKU.
	 * 
	 * @return Money; the current average unit market price per SKU.
	 */
	public Money getUnitMarketPrice() {
		return this.unitMarketPrice;
	}

	/**
	 * Set a new the average unit market price per SKU.
	 * 
	 * @param unitMarketPrice Money; a new value for the current average unit market
	 *                        price per SKU
	 */
	public void setUnitMarketPrice(final Money unitMarketPrice) {
		this.unitMarketPrice = unitMarketPrice;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.name;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.sku);
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("checkstyle:needbraces")
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		return Objects.equals(this.name, other.name) && Objects.equals(this.sku, other.sku);
	}

}
