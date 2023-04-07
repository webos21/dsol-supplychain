package nl.tudelft.simulation.supplychain.role.inventory;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;

/**
 * InventoryActor indicates that the actor has a InventoryRole.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface InventoryActor extends SupplyChainActor
{
    /**
     * Return the inventory role of this actor.
     * @return InventoryRole; the inventory role of this actor
     */
    InventoryRole getInventoryRole();

    /**
     * Initialize the inventory role of this actor. This method can and should only be called once.
     * @param inventoryRole InventoryRole; the inventory role of this actor
     */
    void setInventoryRole(InventoryRole inventoryRole);

}
