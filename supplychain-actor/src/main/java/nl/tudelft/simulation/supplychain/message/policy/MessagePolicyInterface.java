package nl.tudelft.simulation.supplychain.message.policy;

import java.io.Serializable;

import org.djutils.base.Identifiable;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.MessageType;

/**
 * The HandlerInterface defines what any Message Handler should be able to do: handle the receipt of a Message.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface MessagePolicyInterface extends Serializable, Identifiable
{
    /**
     * Handle the content of the message.
     * @param message Message; the message to be handled
     * @return a boolean acknowledgement; true or false
     */
    boolean handleMessage(Message message);

    /**
     * Return the message type that this handler can handle.
     * @return MessageType; the message type that this handler can handle
     */
    MessageType getMessageType();

    /**
     * Return the owner of this handler.
     * @return owner Actor; the owner of this handler.
     */
    Actor getOwner();

}
