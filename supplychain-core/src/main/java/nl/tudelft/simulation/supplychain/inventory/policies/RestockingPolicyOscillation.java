package nl.tudelft.simulation.supplychain.inventory.policies;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.inventory.StockInterface;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * This restocking policy looks at the difference between ordered and stock on hand on one hand, and the committed stock on the
 * other hand. If we committed more than we ordered and have on hand, we overreact and order more products than strictly
 * necessary. This reaction tends to lead in an oscillation of order sizes upstream the supply chain.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RestockingPolicyOscillation extends RestockingPolicyFixed
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** The oscillation margin. */
    protected double oscillationMargin = 0.0;

    /**
     * Construct a new restocking policy based on a safety stock level.
     * @param stock the stock for which the policy holds
     * @param product Product; the product that has to be restocked
     * @param frequency the frequency distribution for restocking
     * @param ceiling fixed ceiling (true) or fixed amount (false)
     * @param amount double; the amount with which or to which stock is supplemented
     * @param includeClaims whether to include the claims in the stock or not
     * @param overReactionMargin the over reaction margin
     * @param maxDeliveryTime the maximum delivery time to use
     */
    public RestockingPolicyOscillation(final StockInterface stock, final Product product,
            final DistContinuousDuration frequency, final boolean ceiling, final double amount, final boolean includeClaims,
            final double overReactionMargin, final Duration maxDeliveryTime)
    {
        super(stock, product, frequency, ceiling, amount, includeClaims, maxDeliveryTime);
        this.oscillationMargin = overReactionMargin;
    }

    /** {@inheritDoc} */
    @Override
    protected void checkStockLevel()
    {
        // just create an internal demand and send it to the owner
        double orderAmount = 0.0;
        double stockLevel = super.stock.getActualAmount(super.product) + super.stock.getOrderedAmount(super.product);
        if (this.includeClaims)
        {
            stockLevel -= super.stock.getClaimedAmount(super.product);
        }
        orderAmount = Math.max(0.0, this.amount - stockLevel);

        if (stockLevel <= 0.0)
        {
            // let's overreact!
            double old = orderAmount;
            orderAmount = Math.ceil(orderAmount + (Math.abs(stockLevel) * (this.oscillationMargin)));
            System.out.println(super.stock.getOwner().getName() + " overreacted: was: " + old + " new: " + orderAmount);
        }

        if (orderAmount > 0.0)
        {
            super.createInternalDemand(orderAmount);
        }
    }

    /**
     * @return the overReactionMargin.
     */
    public double getOscillationMargin()
    {
        return this.oscillationMargin;
    }

    /**
     * @param overReactionMargin The overReactionMargin to set.
     */
    public void setOscillationMargin(final double overReactionMargin)
    {
        this.oscillationMargin = overReactionMargin;
    }
}
