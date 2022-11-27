package nl.tudelft.simulation.supplychain.actor.capabilities;

import nl.tudelft.simulation.supplychain.roles.SellingRoleInterface;

/**
 * SellerInterface.java.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface SellerInterface
{
    /**
     * Get the implementation of the selling role.
     * @return the selling role
     */
    SellingRoleInterface getSellingRole();
}
