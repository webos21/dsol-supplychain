package nl.tudelft.simulation.supplychain.transport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.djunits.Throw;
import org.djutils.base.Identifiable;
import org.djutils.immutablecollections.ImmutableArrayList;
import org.djutils.immutablecollections.ImmutableList;

/**
 * TransportOption describes a way to get goods from A to B. The class can incicate a singular transport mode that transports
 * the goods from A to B, e.g., trucking, or a multimodal option that involves, e.g., a truck to the Port, a containrship to
 * another port, and trucking to the final destination. Each of the modes has a different speed, and each of the transfers will
 * take time (and possibly cost money),
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportOption implements Identifiable, Serializable
{
    /** */
    private static final long serialVersionUID = 20221202L;

    /** the id of the TransportOption. */
    private final String id;

    /** the sequence of TransportSteps. */
    private ImmutableList<TransportOptionStep> transportSteps = new ImmutableArrayList<>(new ArrayList<>());

    /**
     * make a new TransportOption.
     * @param id String; the id of the TransportOption
     */
    public TransportOption(final String id)
    {
        Throw.whenNull(id, "id cannot be null");
        this.id = id;
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the transport steps.
     * @return ImmutableList&lt;TransportOptionStep&gt;; the transport steps
     */
    public ImmutableList<TransportOptionStep> getTransportSteps()
    {
        return this.transportSteps;
    }

    /**
     * Add a transport step.
     * @param transportOptionStep TransportOptionStep; the new transport step
     */
    public void addTransportStep(final TransportOptionStep transportOptionStep)
    {
        Throw.whenNull(transportOptionStep, "transportOptionStep cannot be null");
        List<TransportOptionStep> steps = this.transportSteps.toList();
        steps.add(transportOptionStep);
        this.transportSteps = new ImmutableArrayList<>(steps);
    }

    /**
     * Add a number of transport steps.
     * @param steps List&lt;TransportOptionStep&gt;; the new transport steps
     */
    public void addTransportSteps(final List<TransportOptionStep> steps)
    {
        Throw.whenNull(steps, "steps cannot be null");
        for (TransportOptionStep transportOptionStep : steps)
        {
            addTransportStep(transportOptionStep);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.id, this.transportSteps);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TransportOption other = (TransportOption) obj;
        return Objects.equals(this.id, other.id) && Objects.equals(this.transportSteps, other.transportSteps);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "TransportOption [id=" + this.id + ", transportSteps=" + this.transportSteps + "]";
    }

}
