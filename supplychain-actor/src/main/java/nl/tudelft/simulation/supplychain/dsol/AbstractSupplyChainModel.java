package nl.tudelft.simulation.supplychain.dsol;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.dsol.experiment.StreamInformation;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;

/**
 * AbstractSupplyChainModel is the base model for supply chain models as it contains a registry of actor types and role types.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class AbstractSupplyChainModel extends AbstractDSOLModel<Duration, SCSimulatorInterface>
        implements SCModelInterface
{
    /** */
    private static final long serialVersionUID = 20221127L;

    /**
     * Construct a new supply chain model with a default stream information object.
     * @param simulator SCSimulatorInterface; the simulator to use for this model
     * @throws NullPointerException when simulator is null
     */
    public AbstractSupplyChainModel(final SCSimulatorInterface simulator)
    {
        super(simulator);
    }

    /**
     * Construct a new supply chain model with stream information.
     * @param simulator SCSimulatorInterface; the simulator to use for this model
     * @param streamInformation StreamInformation; the streams that have been prepared in a StreamInformation class
     * @throws NullPointerException when simulator or streamInformation is null
     */
    public AbstractSupplyChainModel(final SCSimulatorInterface simulator, final StreamInformation streamInformation)
    {
        super(simulator, streamInformation);
    }

}
