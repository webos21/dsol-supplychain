package nl.tudelft.simulation.supplychain.actor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djunits.Throw;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.event.EventProducer;

import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.policy.MessagePolicyInterface;

/**
 * AbstractPolicyHandler contains a set of policies for an Actor or Role, and processes Messages using the correct MessagePolicy
 * for each Message.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class AbstractPolicyHandler extends EventProducer implements Serializable
{
    /** */
    private static final long serialVersionUID = 20221205L;

    /** the simulator to schedule simulation events on. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected final SCSimulatorInterface simulator;

    /** the message handling policies. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected final Map<Class<? extends Message>, List<MessagePolicyInterface<? extends Message>>> messagePolicies =
            new LinkedHashMap<>();

    /**
     * Create a new PolicyHandler (the superclass of Actor and Role).
     * @param simulator SCSimulatorInterface; the simulator to schedule simulation events on
     */
    public AbstractPolicyHandler(final SCSimulatorInterface simulator)
    {
        Throw.whenNull(simulator, "simulator cannot be null");
        this.simulator = simulator;
    }

    /**
     * Add a message handling policy to the PolicyHandler.
     * @param policy MessagePolicyInterface&lt;M&gt;; the policy to add
     * @param <M> the message type
     */
    public <M extends Message> void addMessagePolicy(final MessagePolicyInterface<M> policy)
    {
        Throw.whenNull(policy, "policy cannot be null");
        Class<M> messageClass = policy.getMessageClass();
        List<MessagePolicyInterface<? extends Message>> policyList = this.messagePolicies.get(messageClass);
        if (policyList == null)
        {
            policyList = new ArrayList<>();
            this.messagePolicies.put(messageClass, policyList);
        }
        policyList.add(policy);
    }

    /**
     * Remove a message handling policy from the PolicyHandler.
     * @param messageClass Class&lt;M&gt;; the message class of the policy to remove
     * @param policyId String; the id of the policy to remove
     * @param <M> the message type
     */
    public <M extends Message> void removeMessagePolicy(final Class<M> messageClass, final String policyId)
    {
        Throw.whenNull(messageClass, "messageClass cannot be null");
        Throw.whenNull(policyId, "policyId cannot be null");
        List<MessagePolicyInterface<? extends Message>> policyList = this.messagePolicies.get(messageClass);
        if (policyList != null)
        {
            for (MessagePolicyInterface<? extends Message> policy : policyList)
            {
                if (policy.getId().equals(policyId))
                {
                    policyList.remove(policy);
                }
            }
        }
    }

    /**
     * This is the core processing of a message that was received. All appropriate policies of the actor or role are executed.
     * @param message M; the message to process
     * @param <M> The message class to ensure that the message and policy align
     * @return boolean; whether the PolicyHandler processed the message or not
     */
    @SuppressWarnings("unchecked")
    public <M extends Message> boolean processMessage(final M message)
    {
        List<MessagePolicyInterface<? extends Message>> policyList = this.messagePolicies.get(message.getClass());
        if (policyList == null || policyList.size() == 0)
        {
            return false;
        }
        for (MessagePolicyInterface<? extends Message> policy : policyList)
        {
            ((MessagePolicyInterface<M>) policy).handleMessage(message);
        }
        return true;
    }

    /**
     * Send a message to another actor with a delay.
     * @param message message; the message to send
     * @param delay Duration; the time it takes between sending and receiving
     */
    protected void sendMessage(final Message message, final Duration delay)
    {
        this.simulator.scheduleEventRel(delay, this, message.getReceiver(), "receiveMessage", new Object[] {message});
    }

    /**
     * Send a message to another actor without a delay.
     * @param message message; the message to send
     */
    protected void sendMessage(final Message message)
    {
        sendMessage(message, Duration.ZERO);
    }

}
