package nl.tudelft.simulation.supplychain.inventory.policies;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.inventory.StockInterface;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * This restocking policy looks at a safety stock level. As long as the stock level is above the safety stock level, do nothing.
 * Otherwise, order either a fixed amount or replenish until a certain level.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RestockingPolicySafety extends RestockingPolicyFixed
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** The safety stock level. */
    protected double safetyAmount;

    /**
     * Construct a new restocking policy based on a safety stock level.
     * @param stock the stock for which the policy holds
     * @param product Product; the product that has to be restocked
     * @param frequency the frequency distribution for restocking
     * @param ceiling fixed ceiling (true) or fixed amount (false)
     * @param amount double; the amount with which or to which stock is supplemented
     * @param includeClaims whether to include the claims in the stock or not
     * @param safetyAmount the safety stock level for the product
     * @param maxDeliveryTime the maximum delivery time to use
     */
    public RestockingPolicySafety(final StockInterface stock, final Product product, final DistContinuousDuration frequency,
            final boolean ceiling, final double amount, final boolean includeClaims, final double safetyAmount,
            final Duration maxDeliveryTime)
    {
        super(stock, product, frequency, ceiling, amount, includeClaims, maxDeliveryTime);
        this.safetyAmount = safetyAmount;
    }

    /** {@inheritDoc} */
    @Override
    protected void checkStockLevel()
    {
        // check if below safety level; if so, call super.checkStockLevel()
        double stockLevel = super.stock.getActualAmount(super.product) + super.stock.getOrderedAmount(super.product);
        if (this.includeClaims)
        {
            stockLevel -= super.stock.getClaimedAmount(super.product);
        }
        if (stockLevel < this.safetyAmount)
        {
            super.checkStockLevel();
        }
    }

    /**
     * @return the safetyAmount.
     */
    public double getSafetyAmount()
    {
        return this.safetyAmount;
    }

    /**
     * @param safetyAmount The safetyAmount to set.
     */
    public void setSafetyAmount(final double safetyAmount)
    {
        this.safetyAmount = safetyAmount;
    }
}
