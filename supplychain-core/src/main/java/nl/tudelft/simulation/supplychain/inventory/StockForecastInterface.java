package nl.tudelft.simulation.supplychain.inventory;

import org.djunits.value.vdouble.scalar.Time;
import org.djutils.event.EventType;

import nl.tudelft.simulation.supplychain.product.Product;

/**
 * Defines the methods that are needed to calculate stock forecasts.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */

public interface StockForecastInterface extends InventoryInterface
{
    /** An event to indicate that there is a new stock forecast. */
    EventType STOCK_FORECAST_UPDATE_EVENT = new EventType("STOCK_FORECAST_UPDATE_EVENT");

    /**
     * Method changeFutureClaimedAmount.
     * @param product Product; the product
     * @param delta the delta (positive or negative)
     * @param time the time the change is scheduled to take place
     * @return boolean success or not
     */
    boolean changeFutureClaimedAmount(Product product, double delta, Time time);

    /**
     * Method changeFutureOrderedAmount.
     * @param product Product; the product
     * @param delta the delta (positive or negative)
     * @param time the time the change is scheduled to take place
     * @return boolean success or not
     */
    boolean changeFutureOrderedAmount(Product product, double delta, Time time);
}
