package nl.tudelft.simulation.supplychain.transport;

import java.io.Serializable;
import java.util.Objects;

import org.djunits.Throw;
import org.djutils.base.Identifiable;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.product.Sku;

/**
 * TransportRequestStep models one step of a TransportOption. It describes the origin Node and destination Node (as an Actor --
 * any location where trandsfer takes place, such as a port or terminal, is seen as an actor in the logistics network),
 * estimated loading time at the origin Node and unloading time at the destination Node, the mode of transport between origin
 * and destination, and the costs associated with loading, unloading (including storage costs), and transport.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportRequestStep implements Identifiable, Serializable
{
    /** */
    private static final long serialVersionUID = 20221202L;

    /** the identifier for this TransportRequestStep. */
    private final String id;

    /** the actor at the origin (company, port, terminal). */
    private final Actor origin;

    /** the actor at the destination (company, port, terminal). */
    private final Actor destination;

    /** the transport mode between origin and destination. */
    private final TransportMode transportMode;

    /** the SKU that is used for transprort. */
    private final Sku sku;

    /**
     * @param id String; the identifier for this TransportRequestStep
     * @param origin Actor; the actor at the origin (company, port, terminal)
     * @param destination Actor; the actor at the destination (company, port, terminal)
     * @param transportMode TransportMode; the transport mode between origin and destination
     * @param sku Sku; the SKU that is used for transprort
     */
    public TransportRequestStep(final String id, final Actor origin, final Actor destination, final TransportMode transportMode,
            final Sku sku)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(origin, "origin cannot be null");
        Throw.whenNull(destination, "destination cannot be null");
        Throw.whenNull(transportMode, "transportMode cannot be null");
        Throw.whenNull(sku, "sku cannot be null");
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.transportMode = transportMode;
        this.sku = sku;
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the actor at the origin (company, port, terminal).
     * @return origin Actor; the actor at the origin (company, port, terminal)
     */
    public Actor getOrigin()
    {
        return this.origin;
    }

    /**
     * Return the actor at the destination (company, port, terminal).
     * @return destination Actor; the actor at the destination (company, port, terminal)
     */
    public Actor getDestination()
    {
        return this.destination;
    }

    /**
     * Return the transport mode between origin and destination.
     * @return TransportMode; the transport mode between origin and destination
     */
    public TransportMode getTransportMode()
    {
        return this.transportMode;
    }

    /**
     * Return the SKU that is used in this transport request.
     * @return Sku; the SKU that is used in this transport request
     */
    public Sku getSku()
    {
        return this.sku;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.destination, this.id, this.origin, this.sku, this.transportMode);
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
        TransportRequestStep other = (TransportRequestStep) obj;
        return Objects.equals(this.destination, other.destination) && Objects.equals(this.id, other.id)
                && Objects.equals(this.origin, other.origin) && Objects.equals(this.sku, other.sku)
                && Objects.equals(this.transportMode, other.transportMode);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "TransportRequestStep [id=" + this.id + ", origin=" + this.origin + ", destination=" + this.destination
                + ", transportMode=" + this.transportMode + ", sku=" + this.sku + "]";
    }

}
