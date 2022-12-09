package nl.tudelft.simulation.supplychain.finance;

import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.message.handler.DirectMessageHandler;

/**
 * NoBank is a class that can be used for organizations that do not need any banking services.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class NoBank extends Bank
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /**
     * Create a placeholder for a Bank.
     * @param simulator SCSimulatorInterface; needed since it cannot be null
     */
    public NoBank(final SCSimulatorInterface simulator)
    {
        super("nobank", new DirectMessageHandler(), simulator, new OrientedPoint3d(0, 0, 0), "");
    }

}
