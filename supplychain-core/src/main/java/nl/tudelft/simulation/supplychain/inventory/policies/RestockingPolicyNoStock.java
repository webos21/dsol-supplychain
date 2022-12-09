package nl.tudelft.simulation.supplychain.inventory.policies;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.inventory.StockInterface;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

/**
 * RestockingPolicyNoStock.java.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RestockingPolicyNoStock extends RestockingPolicyFixed
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param stock
     * @param product
     */
    public RestockingPolicyNoStock(final StockInterface stock, final Product product)
    {
        super(stock, product, new DistConstantDuration(Duration.POS_MAXVALUE), false, 0.0, false, Duration.ZERO);
    }

}
