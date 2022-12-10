package nl.tudelft.simulation.supplychain.inventory.policies;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.inventory.InventoryInterface;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * This RestockingPolicy either orders fixed amounts of goods at the times indicated by the 'frequency', or supplements the
 * number of products till a fixed amount is reached.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RestockingPolicyFixed extends RestockingPolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** fixed ceiling (true) or fixed amount (false). */
    protected boolean ceiling;

    /** whether to include the claims in the stock or not. */
    protected boolean includeClaims;

    /** the amount in the policy. */
    protected double amount;

    /**
     * Construct a new restocking policy, which works with fixed amounts.
     * @param stock the stock for which the policy holds
     * @param product Product; the product that has to be restocked
     * @param frequency the frequency distribution for restocking
     * @param ceiling fixed ceiling (true) or fixed amount (false)
     * @param amount double; the amount with which or to which stock is supplemented
     * @param includeClaims whether to include the claims in the stock or not
     * @param maxDeliveryTime the maximum delivery time to use
     */
    public RestockingPolicyFixed(final InventoryInterface stock, final Product product, final DistContinuousDuration frequency,
            final boolean ceiling, final double amount, final boolean includeClaims, final Duration maxDeliveryTime)
    {
        super(stock, product, frequency, maxDeliveryTime);
        this.ceiling = ceiling;
        this.amount = amount;
        this.includeClaims = includeClaims;
    }

    /** {@inheritDoc} */
    @Override
    protected void checkStockLevel()
    {
        // just create an internal demand and send it to the owner
        double orderAmount = 0.0;
        if (this.ceiling)
        {
            double stockLevel = super.stock.getActualAmount(super.product) + super.stock.getOrderedAmount(super.product);
            if (this.includeClaims)
            {
                stockLevel -= super.stock.getClaimedAmount(super.product);
            }
            orderAmount = Math.max(0.0, this.amount - stockLevel);
        }
        else
        {
            orderAmount = this.amount;
        }
        if (orderAmount > 0.0)
        {
            createInternalDemand(orderAmount);
        }
    }

    /**
     * @return the amount (ceiling or amount).
     */
    public double getAmount()
    {
        return this.amount;
    }

    /**
     * @param amount The amount or ceiling to set in units of the product.
     */
    public void setAmount(final double amount)
    {
        this.amount = amount;
    }

    /**
     * @return whether we work with a ceiling or fixed amount.
     */
    public boolean isCeiling()
    {
        return this.ceiling;
    }

    /**
     * @param ceiling Set whether we work with a ceiling or fixed amount.
     */
    public void setCeiling(final boolean ceiling)
    {
        this.ceiling = ceiling;
    }

    /**
     * @return whether we include claims in the stock level or not.
     */
    public boolean isIncludeClaims()
    {
        return this.includeClaims;
    }

    /**
     * @param includeClaims Set whether we include claims in the stock level or not.
     */
    public void setIncludeClaims(final boolean includeClaims)
    {
        this.includeClaims = includeClaims;
    }
}
