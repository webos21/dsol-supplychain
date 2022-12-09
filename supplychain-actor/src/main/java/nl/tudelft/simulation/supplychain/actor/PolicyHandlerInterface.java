package nl.tudelft.simulation.supplychain.actor;

import java.io.Serializable;

import org.djutils.event.EventProducerInterface;

import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.policy.MessagePolicyInterface;

/**
 * AbstractPolicyHandler contains a set of policies for an Actor or Role, and processes Messages using the correct MessagePolicy
 * for each Message.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface PolicyHandlerInterface extends EventProducerInterface, Serializable
{
    /**
     * Add a message handling policy to the PolicyHandler.
     * @param policy MessagePolicyInterface&lt;M&gt;; the policy to add
     * @param <M> the message type
     */
    <M extends Message> void addMessagePolicy(MessagePolicyInterface<M> policy);

    /**
     * Remove a message handling policy from the PolicyHandler.
     * @param messageClass Class&lt;M&gt;; the message class of the policy to remove
     * @param policyId String; the id of the policy to remove
     * @param <M> the message type
     */
    <M extends Message> void removeMessagePolicy(Class<M> messageClass, String policyId);

    /**
     * This is the core processing of a message that was received. All appropriate policies of the actor or role are executed.
     * @param message M; the message to process
     * @param <M> The message class to ensure that the message and policy align
     * @return boolean; whether the PolicyHandler processed the message or not
     */
    <M extends Message> boolean processMessage(M message);

}
