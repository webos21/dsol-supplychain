package nl.tudelft.simulation.actor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.actor.message.MessageType;
import nl.tudelft.simulation.actor.message.policy.MessagePolicyInterface;

/**
 * Role is a template for a consistent set of handlers for messages, representing a certain part of the organization, such as
 * sales, inventory, finance, or purchasing.
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
    
    /** the message handlers. */
    private final Map<MessageType, List<MessagePolicyInterface>> messageHandlers = new LinkedHashMap<>();

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
     * Add a message handler to the Role.
     * @param handler HandlerInterface; the handler to add
     */
    public void addHandler(final MessagePolicyInterface handler)
    {
        Throw.whenNull(handler, "handler cannot be null");
        MessageType messageType = handler.getMessageType();
        List<MessagePolicyInterface> handlerList = this.messageHandlers.get(messageType);
        if (handlerList == null)
        {
            handlerList = new ArrayList<>();
            this.messageHandlers.put(messageType, handlerList);
        }
        handlerList.add(handler);
    }
    
    /**
     * Remove a message handler from the Role.
     * @param messageType MessageType; the message type of the handler to remove
     * @param handlerId String; the id of the handler to remove
     */
    public void removeHandler(final MessageType messageType, final String handlerId)
    {
        Throw.whenNull(messageType, "messageType cannot be null");
        Throw.whenNull(handlerId, "handlerId cannot be null");
        List<MessagePolicyInterface> handlerList = this.messageHandlers.get(messageType);
        if (handlerList != null)
        {
            for (MessagePolicyInterface handler : handlerList)
            {
                if (handler.getId().equals(handlerId))
                {
                    handlerList.remove(handler);
                }
            }
        }
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
