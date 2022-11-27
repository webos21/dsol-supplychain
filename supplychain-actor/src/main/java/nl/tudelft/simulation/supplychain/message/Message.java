package nl.tudelft.simulation.supplychain.message;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.actor.Actor;

/**
 * A message, which can be sent from a sender to a receiver. Extend this class to add content.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class Message implements Serializable, Cloneable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** the message type. */
    private final MessageType type;
    
    /** sender of the message (necessary for a possible reply). */
    private final Actor sender;

    /** the receiver of a message. */
    private final Actor receiver;

    /** the timestamp of a message. */
    private final double timestamp;

    /** the message id. */
    private final long id;

    /**
     * Construct a new message.
     * @param type MessageType; the type of message
     * @param sender Actor; the sender
     * @param receiver Actor; the receiver
     */
    public Message(final MessageType type, final Actor sender, final Actor receiver)
    {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = sender.getSimulatorTime().si;
        this.id = sender.getSimulator().getUniqueMessageId();
    }

    /**
     * Return the type of message. 
     * @return MessageType; the message type
     */
    public MessageType getType()
    {
        return this.type;
    }

    /**
     * Return the sender of the message (to allow for a reply to be sent).
     * @return Actor; the sender of the message
     */
    public Actor getSender()
    {
        return this.sender;
    }

    /**
     * Return the receiver of the message.
     * @return Actor; the receiver of the message
     */
    public Actor getReceiver()
    {
        return this.receiver;
    }

    /**
     * Return the (si-value of the) timestamp of the message.
     * @return double; the (si-value of the) timestamp of the message
     */
    public double getTimestamp()
    {
        return this.timestamp;
    }

    /**
     * Return the unique message id.
     * @return long; the unique message id.
     */
    public long getId()
    {
        return this.id;
    }

}
