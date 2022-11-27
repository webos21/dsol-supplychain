package nl.tudelft.simulation.actor.dsol;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.simulators.DEVSRealTimeAnimator;
import nl.tudelft.simulation.naming.context.ContextInterface;

/**
 * SCAnimator extends the DEVSRealTimeAnimator.TimeDoubleUnit, and offers absolute Time for the simulation time in addition to
 * relative duration.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SCAnimator extends DEVSRealTimeAnimator.TimeDoubleUnit implements SCSimulatorInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the start time of the simulator. */
    private final Time absStartTime;

    /** the counter for the unique message id. */
    private long uniqueMessageId = 1000000L;

    /**
     * Construct the SCAnimator that extends the DEVSRealTimeAnimator.TimeDoubleUnit, and offers absolute Time for the
     * simulation time in addition to relative duration.
     * @param id the simulator id
     * @param absStartTime Time; the start time of the simulator
     */
    public SCAnimator(final Serializable id, final Time absStartTime)
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
        return this.uniqueMessageId++;
    }

}
