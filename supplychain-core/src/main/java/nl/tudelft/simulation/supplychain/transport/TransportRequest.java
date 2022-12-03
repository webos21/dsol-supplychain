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
 * TransportRequest describes a request to get goods from A to B. The class can incicate a singular transport mode that
 * transports the goods from A to B, e.g., trucking, or a multimodal request that involves, e.g., a truck to the Port, a
 * containrship to another port, and trucking to the final destination. Each of the modes has a different speed, and each of the
 * transfers will take time (and possibly cost money),
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportRequest implements Identifiable, Serializable
{
    /** */
    private static final long serialVersionUID = 20221202L;

    /** the id of the TransportRequest. */
    private final String id;

    /** the sequence of TransportSteps. */
    private ImmutableList<TransportRequestStep> transportSteps = new ImmutableArrayList<>(new ArrayList<>());

    /**
     * make a new TransportRequest.
     * @param id String; the id of the TransportRequest
     */
    public TransportRequest(final String id)
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
     * @return ImmutableList&lt;TransportRequestStep&gt;; the transport steps
     */
    public ImmutableList<TransportRequestStep> getTransportSteps()
    {
        return this.transportSteps;
    }

    /**
     * Add a transport step.
     * @param transportRequestStep TransportRequestStep; the new transport step
     */
    public void addTransportStep(final TransportRequestStep transportRequestStep)
    {
        Throw.whenNull(transportRequestStep, "transportRequestStep cannot be null");
        List<TransportRequestStep> steps = this.transportSteps.toList();
        steps.add(transportRequestStep);
        this.transportSteps = new ImmutableArrayList<>(steps);
    }

    /**
     * Add a number of transport steps.
     * @param steps List&lt;TransportRequestStep&gt;; the new transport steps
     */
    public void addTransportSteps(final List<TransportRequestStep> steps)
    {
        Throw.whenNull(steps, "steps cannot be null");
        for (TransportRequestStep transportRequestStep : steps)
        {
            addTransportStep(transportRequestStep);
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
        TransportRequest other = (TransportRequest) obj;
        return Objects.equals(this.id, other.id) && Objects.equals(this.transportSteps, other.transportSteps);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "TransportRequest [id=" + this.id + ", transportSteps=" + this.transportSteps + "]";
    }

}
