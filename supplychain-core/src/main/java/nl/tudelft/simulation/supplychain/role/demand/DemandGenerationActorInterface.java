package nl.tudelft.simulation.supplychain.role.demand;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;

/**
 * DemandGenerationActor indicates that the actor has a DemandGenerationRole.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface DemandGenerationActor extends SupplyChainActor
{
    /**
     * Return the demandGeneration role of this actor.
     * @return DemandGenerationRole; the demandGeneration role of this actor
     */
    DemandGenerationRole getDemandGenerationRole();

    /**
     * Initialize the demand generation role of this actor. This method can and should only be called once.
     * @param demandGenerationRole DemandGenerationRole; the demand generation role of this actor
     */
    void setDemandGenerationRole(DemandGenerationRole demandGenerationRole);

}
