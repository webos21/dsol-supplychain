package nl.tudelft.simulation.supplychain.animation;

import java.rmi.RemoteException;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.event.EventInterface;
import org.djutils.event.EventListenerInterface;

import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessage;

/**
 * ContentAnimator.java. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ContentAnimator implements EventListenerInterface
{
    /** the simulator. */
    private final SCSimulatorInterface simulator;

    /**
     * Create an animator for the moving content that listens to the SupplyChainActor.SEND_CONTENT_EVENT.
     * @param simulator the simulator
     */
    public ContentAnimator(final SCSimulatorInterface simulator)
    {
        this.simulator = simulator;
    }

    /**
     * Subscribe ourselves to the SupplyChainActor.SEND_CONTENT_EVENT.
     * @param sca the actor that can send messages
     */
    public void subscribe(final SupplyChainActor sca)
    {
        sca.addListener(this, SupplyChainActor.SEND_MESSAGE_EVENT);
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final EventInterface event) throws RemoteException
    {
        if (event.getType().equals(SupplyChainActor.SEND_MESSAGE_EVENT))
        {
            if (this.simulator instanceof AnimatorInterface)
            {
                Object[] content = (Object[]) event.getContent();
                new ContentAnimation((TradeMessage) content[0], (Duration) content[1]);
            }
        }
    }

    /**
     * @return the simulator
     */
    public SCSimulatorInterface getSimulator()
    {
        return this.simulator;
    }

}
