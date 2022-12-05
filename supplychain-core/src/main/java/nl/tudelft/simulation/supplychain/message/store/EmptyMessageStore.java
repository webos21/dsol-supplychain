package nl.tudelft.simulation.supplychain.message.store;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActorInterface;
import nl.tudelft.simulation.supplychain.message.Message;

/**
 * The EmptyMessageStore does not store anything. E.g., for the YellowPage that does not need to keep track of messages.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class EmptyMessageStore implements MessageStoreInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the owner. */
    private SupplyChainActorInterface owner;

    /**
     * Create a content store that does not store anything. E.g., for the YellowPage that does not need to keep track of
     * messages.
     */
    public EmptyMessageStore()
    {
        // nothing to do.
    }

    /** {@inheritDoc} */
    @Override
    public void setOwner(final SupplyChainActorInterface owner)
    {
        this.owner = owner;
    }

    /** {@inheritDoc} */
    @Override
    public SupplyChainActorInterface getOwner()
    {
        return this.owner;
    }

    /** {@inheritDoc} */
    @Override
    public void addMessage(final Message message, final boolean sent)
    {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeMessage(final Message message, final boolean sent)
    {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeSentReceivedMessage(final Message message, final boolean sent)
    {
        // do nothing
    }

}
