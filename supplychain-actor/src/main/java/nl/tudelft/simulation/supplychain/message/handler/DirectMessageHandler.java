package nl.tudelft.simulation.supplychain.message.handler;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.message.Message;

/**
 * DirectMessageHandler implements a message handler for an actor that immediately handles the message upon receipt.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DirectMessageHandler extends AbstractMessageHandler
{
    /** */
    private static final long serialVersionUID = 20221127L;

    /**
     * Create a message handler for an actor that immediately handles the message upon receipt.
     * @param owner Actor; the Actor to which this message handler belongs
     */
    public DirectMessageHandler(final Actor owner)
    {
        super("DirectMessageHandler", owner);
    }

    /** {@inheritDoc} */
    @Override
    public void handleMessageReceipt(final Message message)
    {
        processMessage(message);
    }

}
