package nl.tudelft.simulation.supplychain.role.producing;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;

/**
 * ProducingActor indicates that the actor has a ProducingRole.
 * <p>
 * Copyright (c) 2022-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface ProducingActor extends SupplyChainActor
{
    /**
     * Return the producing role of this actor.
     * @return ProducingRole; the producing role of this actor
     */
    ProducingRole getProducingRole();

    /**
     * Initialize the producing role of this actor. This method can and should only be called once.
     * @param producingRole ProducingRole; the producing role of this actor
     */
    void setProducingRole(ProducingRole producingRole);

}
