package nl.tudelft.simulation.supplychain.role.buying;

/**
 * BuyingActorInterface indicates that the actor has a BuyingRole.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface BuyingActorInterface
{
    /**
     * Return the buying role of this actor.
     * @return BuyingRole; the buying role of this actor
     */
    BuyingRole getBuyingRole();

    /**
     * Initialize the buying role of this actor. This method can and should only be called once.
     * @param buyingRole BuyingRole; the buying role of this actor
     */
    void setBuyingRole(BuyingRole buyingRole);

}
