package nl.tudelft.simulation.supplychain.message.handler;

import org.djunits.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.RoleInterface;
import nl.tudelft.simulation.supplychain.message.Message;

/**
 * AbstractMessageHandler contains the base implementation of a message handler. A message handler delegates the messages to one
 * or more policies that are able to handle the message. This can be done immediately, after a delay, periodically, or after the
 * appropriate resources are available.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class AbstractMessageHandler implements MessageHandlerInterface
{
    /** */
    private static final long serialVersionUID = 20221126L;

    /** An id for the message handler. */
    private final String id;

    /** The Actor to which this message handler belongs. */
    private Actor owner;

    /**
     * Create a new message handler for an actor.
     * @param id String; an id for the message handler
     */
    public AbstractMessageHandler(final String id)
    {
        Throw.whenNull(id, "id cannot be null");
        this.id = id;
    }

    /** {@inheritDoc} */
    @Override
    public void setOwner(final Actor owner)
    {
        Throw.whenNull(owner, "owner cannot be null");
        Throw.when(this.owner != null, IllegalStateException.class, "MessageHandler.owner already initialized");
        this.owner = owner;
    }

    /**
     * This is the core dispatching method for the processing of a message that was received. All appropriate actor policies and
     * role policies are executed.
     * @param message M; the message to process
     * @param <M> The message class to ensure that the message and policy align
     */
    protected <M extends Message> void dispatchMessageProcessing(final M message)
    {
        boolean processed = false;
        processed |= this.owner.processMessage(message);
        for (RoleInterface role : this.owner.getRoles())
        {
            processed |= role.processMessage(message);
        }
        if (!processed)
        {
            Logger.info(this.toString() + " does not have a handler for " + message.getClass().getSimpleName());
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.id;
    }

    /** {@inheritDoc} */
    @Override
    public Actor getActor()
    {
        return this.owner;
    }

}
