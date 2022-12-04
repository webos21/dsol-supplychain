package nl.tudelft.simulation.supplychain.message.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djunits.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.policy.MessagePolicyInterface;

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
    private final Actor owner;

    /** the message handling policies. */
    private final Map<Class<? extends Message>, List<MessagePolicyInterface<? extends Message>>> messagePolicies =
            new LinkedHashMap<>();

    /**
     * Create a new message queue for an actor.
     * @param id String; an id for the message handler
     * @param owner Actor; the Actor to which this message handler belongs
     */
    public AbstractMessageHandler(final String id, final Actor owner)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(owner, "owner cannot be null");
        this.id = id;
        this.owner = owner;
    }

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
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
     * This is the core processing of a message that was received. All appropriate actor policies are executed.
     * @param message M; the message to process
     * @param <M> The message class to ensure that the message and policy align
     */
    @SuppressWarnings("unchecked")
    protected <M extends Message> void processMessage(final M message)
    {
        List<MessagePolicyInterface<? extends Message>> policyList = this.messagePolicies.get(message.getClass());
        if (policyList == null)
        {
            Logger.info(this.owner + " does not have a handler for " + message.getClass().getSimpleName());
        }
        for (MessagePolicyInterface<? extends Message> policy : policyList)
        {
            ((MessagePolicyInterface<M>) policy).handleMessage(message);
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
