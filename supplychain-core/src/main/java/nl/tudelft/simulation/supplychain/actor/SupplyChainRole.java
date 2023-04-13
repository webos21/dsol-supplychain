package nl.tudelft.simulation.supplychain.actor;

import org.djutils.event.EventProducer;

import nl.tudelft.simulation.supplychain.message.receiver.MessageReceiver;

/**
 * The SupplyChainRole that is aware of the SupplyChainActor rather than the Actor.
 * <p>
 * Copyright (c) 2022-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class SupplyChainRole extends Role
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /**
     * Create a SupplyChainRole that is aware of the SupplyChainActor rather than the Actor.
     * @param id String; the id of the role
     * @param actor SupplyChainActor; the actor to which this role belongs
     * @param messageReceiver MessageReceiver; the message handler to use for processing the messages
     * @param eventProducer EventProducer; a special EventProducer to use, e.g., a RmiEventProducer
     */
    public SupplyChainRole(final String id, final Actor actor, final MessageReceiver messageReceiver,
            final EventProducer eventProducer)
    {
        super(id, actor, messageReceiver, eventProducer);
    }

    /**
     * Create a SupplyChainRole that is aware of the SupplyChainActor rather than the Actor, with a default local event
     * producer..
     * @param id String; the id of the role
     * @param actor SupplyChainActor; the actor to which this role belongs
     * @param messageReceiver MessageReceiver; the message handler to use for processing the messages
     */
    public SupplyChainRole(final String id, final Actor actor, final MessageReceiver messageReceiver)
    {
        super(id, actor, messageReceiver);
    }

    /** {@inheritDoc} */
    @Override
    public Actor getActor()
    {
        return (Actor) super.getActor();
    }

}
