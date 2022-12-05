package nl.tudelft.simulation.supplychain.actor;

import java.io.Serializable;

import org.djutils.exceptions.Throw;

/**
 * Role is a template for a consistent set of policies for handling messages, representing a certain part of the organization,
 * such as sales, inventory, finance, or purchasing.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class Role extends AbstractPolicyHandler implements RoleInterface
{
    /** */
    private static final long serialVersionUID = 20221121L;

    /** the actor to which this role belongs. */
    private final Actor owner;

    /**
     * Create a new Role.
     * @param owner Actor; the actor to which this role belongs
     */
    public Role(final Actor owner)
    {
        super(owner.getSimulator());
        Throw.whenNull(owner, "owner cannot be null");
        this.owner = owner;
    }

    /**
     * Return the actor to which this role belongs.
     * @return owner Actor; the actor to which this role belongs
     */
    @Override
    public Actor getOwner()
    {
        return this.owner;
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return getId();
    }

}
