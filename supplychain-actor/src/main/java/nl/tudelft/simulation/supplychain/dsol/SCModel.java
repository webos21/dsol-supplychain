package nl.tudelft.simulation.supplychain.dsol;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djutils.draw.point.Point;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.StreamInformation;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.dsol.statistics.SimulationStatistic;

/**
 * SCModel.java.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SCModel implements SCModelInterface
{

    /** the counter for the unique message id. */
    private AtomicLong uniqueMessageId = new AtomicLong(1_000_000L);

    /**
     * 
     */
    public SCModel()
    {
    }

    /** {@inheritDoc} */
    @Override
    public void constructModel() throws SimRuntimeException
    {
    }

    /** {@inheritDoc} */
    @Override
    public InputParameterMap getInputParameterMap()
    {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<SimulationStatistic<Duration>> getOutputStatistics()
    {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setStreamInformation(final StreamInformation streamInformation)
    {
    }

    /** {@inheritDoc} */
    @Override
    public StreamInformation getStreamInformation()
    {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public long getUniqueMessageId()
    {
        return this.uniqueMessageId.getAndIncrement();
    }

    /** {@inheritDoc} */
    @Override
    public Length calculateDistance(final Point<?> loc1, final Point<?> loc2)
    {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public SCSimulatorInterface getSimulator()
    {
        return null;
    }

}
