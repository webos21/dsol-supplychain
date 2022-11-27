package nl.tudelft.simulation.supplychain.policy.shipment;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Shipment;

/**
 * When a Shipment comes in, consume it. In other words and in terms of the supply chain simulation: do nothing...
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ShipmentPolicyConsume extends ShipmentPolicy
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * Construct a new ShipmentHandlerConsume handler.
     * @param owner the owner of the handler
     */
    public ShipmentPolicyConsume(final SupplyChainActor owner)
    {
        super(owner);
    }

    /**
     * Do nothing with the incoming cargo. <br>
     * {@inheritDoc}
     */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (!isValidContent(content))
        {
            return false;
        }
        Shipment shipment = (Shipment) content;
        shipment.setInTransit(false);
        shipment.setDelivered(true);
        // do nothing
        return true;
    }
}
