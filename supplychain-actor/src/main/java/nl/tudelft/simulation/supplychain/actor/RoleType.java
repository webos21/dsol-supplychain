package nl.tudelft.simulation.supplychain.actor;

import java.io.Serializable;

import org.djunits.Throw;
import org.djutils.base.Identifiable;

import nl.tudelft.simulation.supplychain.dsol.SCModelInterface;

/**
 * RoleType denotes a type of role, especially for the yellow page searches.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RoleType implements Identifiable, Serializable
{
    /** */
    private static final long serialVersionUID = 20221127L;

    /** the id of the role type. */
    private final String id;

    /**
     * Create a new RoleType with an id, and register the type in the supply chain model.
     * @param id String; the id of the role type
     * @param model SCModelInterface; the model in which to register the role type
     */
    public RoleType(final String id, final SCModelInterface model)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(model, "model cannot be null");
        Throw.when(model.getRoleTypes().containsKey(id), IllegalArgumentException.class,
                "Duplicate registration of role type " + id);
        this.id = id;
        model.registerRoleType(this);
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.id;
    }

}
