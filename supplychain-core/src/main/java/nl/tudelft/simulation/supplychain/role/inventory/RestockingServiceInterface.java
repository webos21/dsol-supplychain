package nl.tudelft.simulation.supplychain.role.inventory;

import java.io.Serializable;

/**
 * Generic restocking policy as the parent of different implementations. It contains the product, inventory, and interval for
 * checking the inventory levels or ordering.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface RestockingServiceInterface extends Serializable
{
    // tagging interface
}
