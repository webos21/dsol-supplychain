package nl.tudelft.simulation.supplychain.message.policy;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.ActorInterface;
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
    private final ActorInterface owner;

    /** the class of messages for which this policy applies. */
    private final Class<M> messageClass;

    /**
     * constructs a new message policy.
     * @param id String; the id of the policy
     * @param owner ActorInterface; the owner of this policy
     * @param messageClass Class&lt;M&gt;; the message type that this policy can process
     */
    public AbstractMessagePolicy(final String id, final ActorInterface owner, final Class<M> messageClass)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(owner, "owner cannot be null");
        Throw.whenNull(messageClass, "messageClass cannot be null");
        this.id = id;
        this.owner = owner;
        this.messageClass = messageClass;
    }

    /**
     * Send a message to another actor with a delay, on behalf of the owner.
     * @param message Message; the message to send
     * @param delay Duration; the time it takes between sending and receiving
     */
    protected void sendMessage(final Message message, final Duration delay)
    {
        getOwner().getSimulator().scheduleEventRel(delay, getOwner(), message.getReceiver(), "receiveMessage",
                new Object[] {message});
    }

    /**
     * Send a message to another actor without a delay, on behalf of the owner.
     * @param message Message; the message to send
     */
    protected void sendMessage(final Message message)
    {
        sendMessage(message, Duration.ZERO);
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.id;
    }

    /** {@inheritDoc} */
    @Override
    public ActorInterface getOwner()
    {
        return this.owner;
    }

    /** {@inheritDoc} */
    @Override
    public Class<M> getMessageClass()
    {
        return this.messageClass;
    }

}
