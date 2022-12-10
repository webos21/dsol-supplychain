package nl.tudelft.simulation.supplychain.role.producing;

import nl.tudelft.simulation.supplychain.actor.SupplyChainRole;
import nl.tudelft.simulation.supplychain.role.inventory.InventoryActorInterface;

/**
 * The producing role is a role that handles the production of products from parts, based on a Bill of Materials (BOM). Parts
 * are sourced from the Inventory, and ready products are added to teh Inventory (Make-to-Stock, MTS) or shipped directly to
 * customers (Make-to-Order, MTO).
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class ProducingRole extends SupplyChainRole
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /**
     * Create a ProducingRole object for an actor.
     * @param owner SupplyChainActorInterface; the owner of this role
     */
    public ProducingRole(final InventoryActorInterface owner)
    {
        super(owner);
    }

}
