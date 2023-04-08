package nl.tudelft.simulation.supplychain.finance;

import org.djutils.draw.point.OrientedPoint2d;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;

/**
 * NoBank is a class that can be used for organizations that do not need any banking services.
 * <p>
 * Copyright (c) 2022-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
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
     * @param model SupplyChainModelInterface; the model to register the 'no bank'
     * @throws ActorAlreadyDefinedException when "nobank" is registered twice
     */
    public NoBank(final SupplyChainModelInterface model) throws ActorAlreadyDefinedException
    {
        super("nobank", "no bank", model, new OrientedPoint2d(0, 0), "");
    }

}
