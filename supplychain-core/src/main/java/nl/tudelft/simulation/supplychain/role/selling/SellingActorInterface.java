package nl.tudelft.simulation.supplychain.role.selling;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;

/**
 * SellingActor indicates that the actor has a SellingRole.
 * <p>
 * Copyright (c) 2022-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface SellingActor extends SupplyChainActor
{
    /**
     * Return the selling role of this actor.
     * @return SellingRole; the selling role of this actor
     */
    SellingRole getSellingRole();

    /**
     * Initialize the selling role of this actor. This method can and should only be called once.
     * @param sellingRole SellingRole; the selling role of this actor
     */
    void setSellingRole(SellingRole sellingRole);

}
