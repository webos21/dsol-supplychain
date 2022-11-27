package nl.tudelft.simulation.actor.message;

import java.util.Objects;

/**
 * MessageType describes the messages that exist in the model.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MessageType
{
    /** the id of the message type. */
    private final String id;
    
    /**
     * Create a new MessageType with this id.
     * @param id String; the id of the message type
     */
    public MessageType(final String id)
    {
        this.id = id;
    }

    /**
     * Return the id of the message type.
     * @return id String; the id of the message type
     */
    public String getId()
    {
        return this.id;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.id);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MessageType other = (MessageType) obj;
        return Objects.equals(this.id, other.id);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "MessageType [id=" + this.id + "]";
    }

}
