package nl.tudelft.simulation.supplychain.role.buying;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.policy.bill.BillPolicy;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyOrder;
import nl.tudelft.simulation.supplychain.policy.orderconfirmation.OrderConfirmationPolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.AbstractShipmentPolicy;

/**
 * The direct buying role is a role that organizes the buying based on a single supplier, and continues from there.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BuyingRoleDirect extends Role implements BuyingRole
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /**
     * Construct a new BuyingRole for Generic Demand - Confirmation - Shipment - Bill.
     * @param owner SupplyChainActor; the actor to which this role belongs
     * @param internalDemandPolicy the internal demand handler
     * @param orderConfirmationPolicy the order confirmation handler
     * @param shipmentPolicy the shipment handler
     * @param billPolicy the bill handler
     */
    public BuyingRoleDirect(final SupplyChainActor owner, final InternalDemandPolicyOrder internalDemandPolicy,
            final OrderConfirmationPolicy orderConfirmationPolicy, final AbstractShipmentPolicy shipmentPolicy,
            final BillPolicy billPolicy)
    {
        super(owner);
        addMessagePolicy(internalDemandPolicy);
        addMessagePolicy(orderConfirmationPolicy);
        addMessagePolicy(shipmentPolicy);
        addMessagePolicy(billPolicy);
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return getOwner().getName() + "-BUYING(direct)";
    }

}
