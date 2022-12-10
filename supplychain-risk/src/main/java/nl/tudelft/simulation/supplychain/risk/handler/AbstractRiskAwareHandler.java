package nl.tudelft.simulation.supplychain.risk.handler;

import org.djutils.event.EventListenerInterface;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;

/**
 * AbstractRiskAwareHandler.java. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class AbstractRiskAwareHandler extends SupplyChainPolicy implements EventListenerInterface
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /**
     * @param owner the owner of this handler
     */
    public AbstractRiskAwareHandler(final SupplyChainActor owner)
    {
        super(owner);
    }

}
