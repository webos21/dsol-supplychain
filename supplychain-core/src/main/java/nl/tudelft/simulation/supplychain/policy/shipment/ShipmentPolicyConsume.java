package nl.tudelft.simulation.supplychain.policy.shipment;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.trade.Shipment;

/**
 * When a Shipment comes in, consume it. In other words and in terms of the supply chain simulation: do nothing...
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ShipmentPolicyConsume extends AbstractShipmentPolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /**
     * Construct a new ShipmentHandlerConsume handler.
     * @param owner SupplyChainActor; the owner of the policy
     */
    public ShipmentPolicyConsume(final SupplyChainActor owner)
    {
        super("ShipmentPolicyConsume", owner);
    }

    /**
     * Do nothing with the incoming cargo. <br>
     * {@inheritDoc}
     */
    @Override
    public boolean handleMessage(final Message message)
    {
        if (!isValidMessage(message))
        {
            return false;
        }
        Shipment shipment = (Shipment) message;
        shipment.setInTransit(false);
        shipment.setDelivered(true);
        // do nothing
        return true;
    }
}
