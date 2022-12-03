package nl.tudelft.simulation.supplychain.actor;

import java.io.Serializable;

import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.policy.MessagePolicyInterface;

/**
 * Role is a template for a consistent set of policies for handling messages, representing a certain part of the organization,
 * such as sales, inventory, finance, or purchasing.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class Role implements Serializable, Identifiable
{
    /** */
    private static final long serialVersionUID = 20221121L;

    /** the type of role (e.g., for yellow page requests). */
    private final RoleType roleType;

    /** the role id. */
    private final String id;

    /** the actor to which this role belongs. */
    private final Actor owner;

    /**
     * Create a new Role.
     * @param roleType RoleType; the type of role
     * @param id String; the id of the role
     * @param owner Actor; the actor to which this role belongs
     */
    public Role(final RoleType roleType, final String id, final Actor owner)
    {
        Throw.whenNull(roleType, "roleType cannot be null");
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(owner, "owner cannot be null");
        this.roleType = roleType;
        this.id = id;
        this.owner = owner;
    }

    /**
     * Add a message handling policy to the Role.
     * @param policy MessagePolicyInterface&lt;M&gt;; the policy to add
     * @param <M> the message type
     */
    public <M extends Message> void addMessagePolicy(final MessagePolicyInterface<M> policy)
    {
        this.owner.addMessagePolicy(policy);
    }

    /**
     * Remove a message handling policy from the Role.
     * @param messageClass Class&lt;M&gt;; the message class of the policy to remove
     * @param policyId String; the id of the policy to remove
     * @param <M> the message type
     */
    public <M extends Message> void removeMessagePolicy(final Class<M> messageClass, final String policyId)
    {
        this.owner.removeMessagePolicy(messageClass, policyId);
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the role type (e.g., for yellow page requests).
     * @return roleType RoleType; the roletype
     */
    public RoleType getRoleType()
    {
        return this.roleType;
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
