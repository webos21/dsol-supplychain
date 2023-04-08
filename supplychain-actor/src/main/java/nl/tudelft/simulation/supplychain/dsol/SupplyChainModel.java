package nl.tudelft.simulation.supplychain.dsol;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.djunits.Throw;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.dsol.experiment.StreamInformation;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.ActorNotFoundException;

/**
 * SupplyChainModel is the default model implementation from which model implementations can extend. It defines an empty set of
 * input parameters, an empty set of output statistics, an empty set of actors,
 * <p>
 * Copyright (c) 2022-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class SupplyChainModel extends AbstractDSOLModel<Duration, SupplyChainSimulatorInterface>
        implements SupplyChainModelInterface
{
    /** */
    private static final long serialVersionUID = 20230407L;

    /** the counter for the unique message id. */
    private AtomicLong uniqueMessageId = new AtomicLong(1_000_000L);

    /** the map of actors based on their id. */
    private Map<String, Actor> actorMap = new LinkedHashMap<>();

    /**
     * Create a supply chain model with a specific set of random streams for this replication.
     * @param simulator SupplyChainSimulatorInterface; the simulator
     * @param streamInformation StreamInformation; information about the random streams to use in a replication
     */
    public SupplyChainModel(final SupplyChainSimulatorInterface simulator, final StreamInformation streamInformation)
    {
        super(simulator, streamInformation);
    }

    /**
     * Create a supply chain model with a default set of random streams for this replication.
     * @param simulator SupplyChainSimulatorInterface; the simulator
     */
    public SupplyChainModel(final SupplyChainSimulatorInterface simulator)
    {
        super(simulator);
    }

    /** {@inheritDoc} */
    @Override
    public long getUniqueMessageId()
    {
        return this.uniqueMessageId.getAndIncrement();
    }

    /** {@inheritDoc} */
    @Override
    public Actor getActor(final String id) throws ActorNotFoundException
    {
        Throw.when(!this.actorMap.containsKey(id), ActorNotFoundException.class, id);
        return this.actorMap.get(id);
    }

}
