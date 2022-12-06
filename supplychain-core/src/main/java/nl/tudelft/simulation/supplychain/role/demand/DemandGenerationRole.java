package nl.tudelft.simulation.supplychain.role.demand;

import nl.tudelft.simulation.supplychain.actor.RoleInterface;

/**
 * The demand generation role is a role for customers, markets, and other actors that have an autonomous generation of demand
 * for products. This is different from the InventoryRole, where demand generation is triggered by depletion of stock.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface DemandGenerationRole extends RoleInterface
{
    // tagging interface for now.
}
