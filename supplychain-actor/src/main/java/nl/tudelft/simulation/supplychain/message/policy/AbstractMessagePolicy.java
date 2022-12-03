package nl.tudelft.simulation.supplychain.message.policy;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.message.Message;

/**
 * An abstract definition of a message policy with an owner.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <M> the message class for which this policy applies
 */
public abstract class AbstractMessagePolicy<M extends Message> implements MessagePolicyInterface<M>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221126L;

    /** the id of the policy. */
    private final String id;

    /** the owner of this policy. */
    private final Actor owner;

    /** the class of messages for which this policy applies. */
    private final Class<? extends M> messageClass;

    /**
     * constructs a new message policy.
     * @param id String; the id of the policy
     * @param owner Actor; the owner of this policy
     * @param messageClass MessageType; the message type that this policy can process
     */
    public AbstractMessagePolicy(final String id, final Actor owner, final Class<? extends M> messageClass)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(owner, "owner cannot be null");
        Throw.whenNull(messageClass, "messageType cannot be null");
        this.id = id;
        this.owner = owner;
        this.messageClass = messageClass;
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.id;
    }

    /** {@inheritDoc} */
    @Override
    public Actor getOwner()
    {
        return this.owner;
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends M> getMessageClass()
    {
        return this.messageClass;
    }

}
