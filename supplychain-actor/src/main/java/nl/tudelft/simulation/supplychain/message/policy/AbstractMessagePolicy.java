package nl.tudelft.simulation.actor.message.policy;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.actor.Actor;
import nl.tudelft.simulation.actor.message.MessageType;

/**
 * An abstract definition of a handler with an owner. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class AbstractMessagePolicy implements MessagePolicyInterface
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221126L;

    /** the id of the handler. */
    private final String id;
    
    /** the owner of this handler. */
    private final Actor owner;
    
    /** the message type that this handler can handle. */
    private final MessageType messageType;

    /**
     * constructs a new Handler.
     * @param id String; the id of the handler
     * @param owner Actor; the owner of this handler
     * @param messageType MessageType; the message type that this handler can handle
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
