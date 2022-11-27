package nl.tudelft.simulation.supplychain.actor.capabilities;

import java.util.Map;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * BuyerYPInterface.java.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface BuyerOrderInterface extends BuyerInterface
{
    /**
     * get the supplier per product where we initiate the buying chain by placing a direct order.
     * @return the unique supplier per product
     */
    Map<Product, SupplyChainActor> getSupplier();
}
