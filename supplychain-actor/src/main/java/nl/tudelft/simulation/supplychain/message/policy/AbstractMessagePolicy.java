package nl.tudelft.simulation.supplychain.message.policy;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.message.MessageType;

/**
 * An abstract definition of a message policy with an owner.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class AbstractMessagePolicy implements MessagePolicyInterface
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221126L;

    /** the id of the policy. */
    private final String id;

    /** the owner of this policy. */
    private final Actor owner;

    /** the message type that this policy can handle. */
    private final MessageType messageType;

    /**
     * constructs a new message policy.
     * @param id String; the id of the policy
     * @param owner Actor; the owner of this policy
     * @param messageType MessageType; the message type that this policy can process
     */
    public AbstractMessagePolicy(final String id, final Actor owner, final MessageType messageType)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(owner, "owner cannot be null");
        Throw.whenNull(messageType, "messageType cannot be null");
        this.id = id;
        this.owner = owner;
        this.messageType = messageType;
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
    public MessageType getMessageType()
    {
        return this.messageType;
    }

}
