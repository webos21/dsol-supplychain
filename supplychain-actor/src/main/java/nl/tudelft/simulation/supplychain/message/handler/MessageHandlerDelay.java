package nl.tudelft.simulation.supplychain.message.handler;

import org.djunits.Throw;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.message.Message;

/**
 * MessageHandlerDelay implements a message handler for an actor that handles messages after a (stochastic) delay time.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MessageHandlerDelay extends AbstractMessageHandler
{
    /** */
    private static final long serialVersionUID = 20221127L;

    /** the delay distribution that can be changed, e.g., for implementing administrative delays. */
    private DistContinuousDuration delayDistribution;

    /**
     * Create a message handler for an actor that immediately handles the message upon receipt.
     * @param owner Actor; the Actor to which this message handler belongs
     * @param delayDistribution DistContinuousDuration; the delay distribution for handling messages (note that the distribution
     *            can be changed, e.g., for implementing administrative delays)
     */
    public MessageHandlerDelay(final Actor owner, final DistContinuousDuration delayDistribution)
    {
        super("DirectMessageHandler", owner);
        Throw.whenNull(delayDistribution, "delayDistribution cannot be null");
        this.delayDistribution = delayDistribution;
    }

    /** {@inheritDoc} */
    @Override
    public void handleMessageReceipt(final Message message)
    {
        message.getSender().getSimulator().scheduleEventRel(this.delayDistribution.draw(), getActor(), this, "processMessage",
                new Object[] {message});
    }

    /**
     * Return the delay distribution for handling messages.
     * @return DistContinuousDuration; the delay distribution
     */
    public DistContinuousDuration getDelayDistribution()
    {
        return this.delayDistribution;
    }

    /**
     * Set a new delay distribution for handling messages.
     * @param delayDistribution DistContinuousDuration; the new delay distribution
     */
    public void setDelayDistribution(final DistContinuousDuration delayDistribution)
    {
        Throw.whenNull(delayDistribution, "delayDistribution cannot be null");
        this.delayDistribution = delayDistribution;
    }

}
