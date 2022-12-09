package nl.tudelft.simulation.supplychain.role.selling;

/**
 * SellingActorInterface indicates that the actor has a SellingRole.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, ProducingActorInterfaceDelft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface SellingActorInterface
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
