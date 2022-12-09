package nl.tudelft.simulation.supplychain.reference;

import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.policy.yp.YellowPageRequestPolicy;
import nl.tudelft.simulation.supplychain.yellowpage.YellowPageActor;

/**
 * Reference implementation of the YellowPage.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class YellowPage extends YellowPageActor
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /**
     * Create a YellowPage actor.
     * @param name String; the name of the Supplier
     * @param simulator SCSimulatorInterface; the simulator
     * @param position OrientedPoint3d; the locatrion of the actor on the map or grid
     * @param messageStore TradeMessageStoreInterface; the messageStore for the messages
     * @param handlingTime DistContinuousDuration; the duration to handle a request
     */
    public YellowPage(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position,
            final TradeMessageStoreInterface messageStore, final DistContinuousDuration handlingTime)
    {
        super(name, simulator, position, messageStore);
        addMessagePolicy(new YellowPageRequestPolicy(this, handlingTime));
    }

}
