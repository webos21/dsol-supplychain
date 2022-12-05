package nl.tudelft.simulation.supplychain.actor;

import java.io.Serializable;

import org.djutils.base.Identifiable;

/**
 * Role is a template for a consistent set of policies for handling messages, representing a certain part of the organization,
 * such as sales, inventory, finance, or purchasing.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface RoleInterface extends PolicyHandlerInterface, Identifiable
{
    /**
     * Return the actor to which this role belongs.
     * @return owner Actor; the actor to which this role belongs
     */
    Actor getOwner();

    /** {@inheritDoc} */
    @Override
    default Serializable getSourceId()
    {
        return getId();
    }

}
