package nl.tudelft.simulation.supplychain.role.buying;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.actor.SupplyChainRole;

/**
 * The buying role is a role that can handle several types of message content: internal demand, order confirmation, bill, and
 * shipment. Depending on the extension of the BuyingRole, which actually indicates the type if InternalDemandPolicy used,
 * several other messages can be handled as well. For the InternalDemandPolicyOrder, no extra types are necessary. For the
 * InternalDemandhandlerRFQ, a Quote has to be handled as well. For an InternalDemandhandlerYP, a YellowPageAnswer can be
 * received, and has to be handled.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class BuyingRole extends SupplyChainRole
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /**
     * Create a BuyingRole object for an actor.
     * @param owner SupplyChainActor; the owner of this role
     */
    public BuyingRole(final SupplyChainActor owner)
    {
        super(owner);
    }
}
