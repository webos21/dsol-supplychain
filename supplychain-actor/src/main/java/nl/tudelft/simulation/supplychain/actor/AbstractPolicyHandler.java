package nl.tudelft.simulation.supplychain.actor;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djunits.Throw;
import org.djutils.event.EventListenerMap;
import org.djutils.event.EventProducer;
import org.djutils.event.LocalEventProducer;

import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.policy.MessagePolicyInterface;

/**
 * AbstractPolicyHandler contains a set of policies for an Actor or Role, and processes Messages using the correct MessagePolicy
 * for each Message.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class AbstractPolicyHandler implements PolicyHandlerInterface
{
    /** */
    private static final long serialVersionUID = 20221205L;

    /** the simulator to schedule simulation events on. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected final SupplyChainSimulatorInterface simulator;

    /** the message handling policies. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected final Map<Class<? extends Message>, List<MessagePolicyInterface<? extends Message>>> messagePolicies =
            new LinkedHashMap<>();

    /** the embedded event producer. */
    private final EventProducer eventProducer;

    /**
     * Create a new PolicyHandler (the superclass of Actor and Role) with a LocalEventProducer.
     * @param simulator SupplyChainSimulatorInterface; the simulator to schedule simulation events on
     */
    public AbstractPolicyHandler(final SupplyChainSimulatorInterface simulator)
    {
        this(simulator, new LocalEventProducer());
    }

    /**
     * Create a new PolicyHandler (the superclass of Actor and Role) with a special EventProducer.
     * @param simulator SupplyChainSimulatorInterface; the simulator to schedule simulation events on
     * @param eventProducer EventProducer; a special EventProducer to use, e.g., a RmiEventProducer
     */
    public AbstractPolicyHandler(final SupplyChainSimulatorInterface simulator, final EventProducer eventProducer)
    {
        Throw.whenNull(simulator, "simulator cannot be null");
        this.simulator = simulator;
        this.eventProducer = eventProducer;
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

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
    public EventListenerMap getEventListenerMap() throws RemoteException
    {
        return this.eventProducer.getEventListenerMap();
    }

}
