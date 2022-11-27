package nl.tudelft.simulation.supplychain.actor.capabilities;

import java.util.Map;
import java.util.Set;

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
public interface BuyerRFQInterface extends BuyerInterface
{
    /**
     * get the suppliers per product where we initiate the buying chain.
     * @return the suppliers per product
     */
    Map<Product, Set<SupplyChainActor>> getSuppliers();
}
