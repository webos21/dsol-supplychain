package nl.tudelft.simulation.supplychain.policy.internaldemand;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.policy.SupplyChainHandler;
import nl.tudelft.simulation.supplychain.stock.StockInterface;

/**
 * The abstract InternalDemandHandler class provides the general methods that all InternalDemandHandler classes need, such as
 * checking whether the message is really an InternalDemand.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class InternalDemandPolicy extends SupplyChainHandler
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the handling time distribution to handle internal demand */
    protected DistContinuousDuration handlingTime;

    /** the stock for changing 'ordered amount' */
    protected StockInterface stock;

    /**
     * Construct a new InternalDemandHandler.
     * @param owner the SupplyChainActor that has this policy.
     * @param handlingTime the distribution of the time to handle an internal demand
     * @param stock the stock for being able to change the ordered amount
     */
    public InternalDemandPolicy(final SupplyChainActor owner, final DistContinuousDuration handlingTime,
            final StockInterface stock)
    {
        super(owner);
        this.handlingTime = handlingTime;
        this.stock = stock;
    }

    /**
     * @param handlingTime The handlingTime to set.
     */
    public void setHandlingTime(final DistContinuousDuration handlingTime)
    {
        this.handlingTime = handlingTime;
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends Content> getContentClass()
    {
        return InternalDemand.class;
    }
}
