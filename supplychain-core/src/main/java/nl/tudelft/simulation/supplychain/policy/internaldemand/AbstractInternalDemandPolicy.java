package nl.tudelft.simulation.supplychain.policy.internaldemand;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.inventory.InventoryInterface;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;

/**
 * The abstract InternalDemandPolicy class provides the general methods that all InternalDemandPolicy classes need, such as
 * checking whether the message is really an InternalDemand.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class AbstractInternalDemandPolicy extends SupplyChainPolicy<InternalDemand>
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the handling time distribution to handle internal demand. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected DistContinuousDuration handlingTime;

    /** the stock for changing 'ordered amount. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected InventoryInterface stock;

    /**
     * Construct a new InternalDemandPolicy.
     * @param id String; the id of the policy
     * @param owner the SupplyChainActor that has this policy.
     * @param handlingTime the distribution of the time to handle an internal demand
     * @param stock the stock for being able to change the ordered amount
     */
    public AbstractInternalDemandPolicy(final String id, final SupplyChainActor owner,
            final DistContinuousDuration handlingTime, final InventoryInterface stock)
    {
        super(id, owner, InternalDemand.class);
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

}
