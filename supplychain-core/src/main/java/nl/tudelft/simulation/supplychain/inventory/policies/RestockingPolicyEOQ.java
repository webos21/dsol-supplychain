package nl.tudelft.simulation.supplychain.stock.policies;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;

/**
 * Restocking according to economic order quantity. Needs fixed and variable costs when it has to be implemented. For that
 * reason: still abstract...
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class RestockingPolicyEOQ extends RestockingPolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /**
     * @param stock the stock for which the policy holds
     * @param product Product; the product that has to be restocked
     * @param frequency the frequency distribution for restocking or checking
     * @param maxDeliveryTime the maximum delivery time to use
     */
    public RestockingPolicyEOQ(final StockInterface stock, final Product product, final DistContinuousDuration frequency,
            final Duration maxDeliveryTime)
    {
        super(stock, product, frequency, maxDeliveryTime);
    }

    /** {@inheritDoc} */
    @Override
    protected void checkStockLevel()
    {
        // TODO: EOQ
        Logger.warn("checkStockLevel - EOQ not yet implemented...");
    }
}
