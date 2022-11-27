package nl.tudelft.simulation.supplychain.actor.capabilities;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.demand.Demand;
import nl.tudelft.simulation.supplychain.demand.DemandGeneration;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * AutonomousDemandInterface.java.
 * <p>
 * Copyright (c) 2019-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface AutonomousDemandInterface
{
    /**
     * Get the autonomous demand generation object.
     * @return DemandGeneration; the autonomous demand generation object
     */
    DemandGeneration getDemandGeneration();

    /**
     * Add a demand generator for the autonomous demand. with a continuous amount of product.
     * @param product the product
     * @param interval the distribution for the demand generation interval
     * @param amount the amount of product to order
     * @param earliestDeliveryDurationDistribution the earliest delivery date distribution
     * @param latestDeliveryDurationDistribution the latest delivery date distribution
     */
    default void addDemand(Product product, DistContinuousDuration interval, DistContinuous amount,
            DistContinuousDuration earliestDeliveryDurationDistribution,
            DistContinuousDuration latestDeliveryDurationDistribution)
    {
        Demand demand =
                new Demand(product, interval, amount, earliestDeliveryDurationDistribution, latestDeliveryDurationDistribution);
        getDemandGeneration().addDemandGenerator(product, demand);
    }

    /**
     * Add a demand generator for the autonomous demand. with a discrete amount of product.
     * @param product the product
     * @param interval the distribution for the demand generation interval
     * @param amount the amount of product to order
     * @param earliestDeliveryDurationDistribution the earliest delivery date distribution
     * @param latestDeliveryDurationDistribution the latest delivery date distribution
     */
    default void addDemand(Product product, DistContinuousDuration interval, DistDiscrete amount,
            DistContinuousDuration earliestDeliveryDurationDistribution,
            DistContinuousDuration latestDeliveryDurationDistribution)
    {
        Demand demand =
                new Demand(product, interval, amount, earliestDeliveryDurationDistribution, latestDeliveryDurationDistribution);
        getDemandGeneration().addDemandGenerator(product, demand);
    }

}
