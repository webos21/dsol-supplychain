package nl.tudelft.simulation.actor.message.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djunits.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.actor.Actor;
import nl.tudelft.simulation.actor.message.Message;
import nl.tudelft.simulation.actor.message.MessageType;
import nl.tudelft.simulation.actor.message.policy.MessagePolicyInterface;

/**
 * AbstractMessageHandler contains the base implementation of a message handler.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
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
    private final Map<MessageType, List<MessagePolicyInterface>> messagePolicies = new LinkedHashMap<>();

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
    public void addMessagePolicy(final MessagePolicyInterface policy)
    {
        Throw.whenNull(policy, "policy cannot be null");
        MessageType messageType = policy.getMessageType();
        List<MessagePolicyInterface> policyList = this.messagePolicies.get(messageType);
        if (policyList == null)
        {
            policyList = new ArrayList<>();
            this.messagePolicies.put(messageType, policyList);
        }
        policyList.add(policy);
    }
    
    /** {@inheritDoc} */
    @Override
    public void removeMessagePolicy(final MessageType messageType, final String policyId)
    {
        Throw.whenNull(messageType, "messageType cannot be null");
        Throw.whenNull(policyId, "policyId cannot be null");
        List<MessagePolicyInterface> policyList = this.messagePolicies.get(messageType);
        if (policyList != null)
        {
            for (MessagePolicyInterface policy : policyList)
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
     * @param message 
     */
    protected void processMessage(final Message message)
    {
        List<MessagePolicyInterface> policyList = this.messagePolicies.get(message.getType());
        if (policyList == null)
        {
            Logger.info(this.owner + " does not have a handler for " + message.getType());
        }
        for (MessagePolicyInterface policy : policyList)
        {
            policy.handleMessage(message);
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
    public Actor getOwner()
    {
        return this.owner;
    }

}
