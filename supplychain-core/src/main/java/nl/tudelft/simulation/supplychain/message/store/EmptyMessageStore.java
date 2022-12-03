package nl.tudelft.simulation.supplychain.message.store;

import java.util.List;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.MessageType;

/**
 * The EmptyMessageStore does not store anything. E.g., for the YellowPage that does not need to keep track of messages.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class EmptyMessageStore implements MessageStoreInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the owner. */
    private SupplyChainActor owner;

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
    public void setOwner(final SupplyChainActor owner)
    {
        this.owner = owner;
    }

    /** {@inheritDoc} */
    @Override
    public SupplyChainActor getOwner()
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

    /** {@inheritDoc} */
    @Override
    public void removeAllMessages(final long internalDemandId)
    {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public List<Message> getMessageList(final long internalDemandId, final MessageType type)
    {
        return List.of();
    }

    /** {@inheritDoc} */
    @Override
    public List<Message> getMessageList(final long internalDemandId, final MessageType type, final boolean sent)
    {
        return List.of();
    }

}
