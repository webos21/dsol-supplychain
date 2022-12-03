package nl.tudelft.simulation.supplychain.message;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.Actor;

/**
 * A message, which can be sent from a sender to a receiver. Extend this class to add content.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class Message implements Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221203L;

    /** sender of the message (necessary for a possible reply). */
    private final Actor sender;

    /** the receiver of a message. */
    private final Actor receiver;

    /** the timestamp of a message. */
    private final Time timestamp;

    /** the unqiue message id. */
    private final long uniqueId;

    /**
     * Construct a new message.
     * @param sender Actor; the sender
     * @param receiver Actor; the receiver
     */
    public Message(final Actor sender, final Actor receiver)
    {
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = sender.getSimulatorTime();
        this.uniqueId = sender.getSimulator().getUniqueMessageId();
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
     * Return the timestamp of the message.
     * @return Time; the timestamp of the message
     */
    public Time getTimestamp()
    {
        return this.timestamp;
    }

    /**
     * Return the unique message id.
     * @return long; the unique message id.
     */
    public long getUniqueId()
    {
        return this.uniqueId;
    }

}
