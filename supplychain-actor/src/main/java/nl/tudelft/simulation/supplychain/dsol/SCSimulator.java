package nl.tudelft.simulation.supplychain.dsol;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.naming.context.ContextInterface;

/**
 * SCSimulator extends the DevsSimulator, and offers absolute Time for the simulation time in addition to relative duration.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SCSimulator extends DevsSimulator<Duration> implements SCSimulatorInterface
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the start time of the simulator. */
    private final Time absStartTime;

    /** the counter for the unique message id. */
    private AtomicLong uniqueMessageId = new AtomicLong(1_000_000L);

    /**
     * Construct the SCSimulator that extends the DevsSimulator, and offers absolute Time for the simulation time in addition to
     * relative duration.
     * @param id the simulator id
     * @param absStartTime Time; the start time of the simulator
     */
    public SCSimulator(final Serializable id, final Time absStartTime)
    {
        super(id);
        this.absStartTime = absStartTime;
    }

    /** {@inheritDoc} */
    @Override
    public ContextInterface getContext()
    {
        return getReplication().getContext();
    }

    /** {@inheritDoc} */
    @Override
    public Time getAbsStartTime()
    {
        return this.absStartTime;
    }

    /** {@inheritDoc} */
    @Override
    public long getUniqueMessageId()
    {
        return this.uniqueMessageId.getAndIncrement();
    }

    /** {@inheritDoc} */
    @Override
    public SCModelInterface getModel()
    {
        return (SCModelInterface) super.getModel();
    }

}
