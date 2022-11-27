package nl.tudelft.simulation.actor;

import java.io.Serializable;

import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.actor.message.MessageType;
import nl.tudelft.simulation.actor.message.policy.MessagePolicyInterface;

/**
 * Role is a template for a consistent set of policies for handling messages, representing a certain part of the organization,
 * such as sales, inventory, finance, or purchasing.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class Role implements Serializable, Identifiable
{
    /** */
    private static final long serialVersionUID = 20221121L;

    /** the role id. */
    private final String id;

    /** the actor to which this role belongs. */
    private final Actor owner;

    /**
     * Create a new Role.
     * @param id String; the id of the role
     * @param owner Actor; the actor to which this role belongs
     */
    public Role(final String id, final Actor owner)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(owner, "owner cannot be null");
        this.id = id;
        this.owner = owner;
    }

    /**
     * Add a message handling policy to the Role.
     * @param policy MessagePolicyInterface; the policy to add
     */
    public void addMessagePolicy(final MessagePolicyInterface policy)
    {
        this.owner.addMessagePolicy(policy);
    }

    /**
     * Remove a message handling policy from the Role.
     * @param messageType MessageType; the message type of the policy to remove
     * @param policyId String; the id of the policy to remove
     */
    public void removeMessagePolicy(final MessageType messageType, final String policyId)
    {
        this.owner.removeMessagePolicy(messageType, policyId);
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the actor to which this role belongs.
     * @return owner Actor; the actor to which this role belongs
     */
    public Actor getOwner()
    {
        return this.owner;
    }

}
