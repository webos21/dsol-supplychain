package nl.tudelft.simulation.actor.message.handler;

import java.io.Serializable;

import org.djutils.base.Identifiable;

import nl.tudelft.simulation.actor.Actor;
import nl.tudelft.simulation.actor.message.Message;
import nl.tudelft.simulation.actor.message.MessageType;
import nl.tudelft.simulation.actor.message.policy.MessagePolicyInterface;

/**
 * MessageHandlerInterface priovides the contract for a message (mailbox) handler.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface MessageHandlerInterface extends Identifiable, Serializable
{
    /**
     * Return the Actor to which this message handler belongs.
     * @return Actor; the Actor to which this message handler belongs
     */
    Actor getOwner();

    /**
     * Add a message handling policy to the handler.
     * @param policy MessagePolicyInterface; the policy to add
     */
    void addMessagePolicy(MessagePolicyInterface policy);

    /**
     * Remove a message handling policy from the handler.
     * @param messageType MessageType; the message type of the policy to remove
     * @param policyId String; the id of the policy to remove
     */
    void removeMessagePolicy(MessageType messageType, String policyId);

    /**
     * Handle an incoming message. This can be storing the message, handling it with a delay, or immediately handling it.
     * Typically, messages are handled by a MessagePolicy.
     * @param message Message; the message to handle
     */
    void handleMessageReceipt(Message message);

}
