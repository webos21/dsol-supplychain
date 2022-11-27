package nl.tudelft.simulation.supplychain.actor.capabilities;

import nl.tudelft.simulation.supplychain.stock.StockInterface;

/**
 * StockKeepingInterface.java.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface StockKeeperInterface
{
    /**
     * The stock keeping capability means that the stock has to be retrievable.
     * @return the stock of the actor
     */
    StockInterface getStock();
}
