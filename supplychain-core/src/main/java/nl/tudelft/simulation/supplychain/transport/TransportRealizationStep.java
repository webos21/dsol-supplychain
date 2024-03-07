package nl.tudelft.simulation.supplychain.transport;

import java.io.Serializable;
import java.util.Objects;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.product.Sku;

/**
 * TransportRealizationStep models one step of a TransportRealization. It
 * describes the origin Node and destination Node (as an Actor -- any location
 * where trandsfer takes place, such as a port or terminal, is seen as an actor
 * in the logistics network), loading time at the origin Node and unloading time
 * at the destination Node, the mode of transport between origin and
 * destination, and the costs associated with loading, unloading (including
 * storage costs), and transport.
 * <p>
 * Copyright (c) 2022-2023 Delft University of Technology, Delft, the
 * Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportRealizationStep implements Identifiable, Serializable {
	/** */
	private static final long serialVersionUID = 20221202L;

	/** the identifier for this TransportStep. */
	private final String id;

	/** the actor at the origin (company, port, terminal). */
	private final Actor origin;

	/** the actor at the destination (company, port, terminal). */
	private final Actor destination;

	/** the transport mode between origin and destination. */
	private final TransportMode transportMode;

	/** the SKU that is used for transprort. */
	private final Sku sku;

	/** the time to load a SKU at the origin (including typical waiting times). */
	private final Duration loadingTime;

	/**
	 * the time to unload a SKU at the destination (including typical waiting
	 * times).
	 */
	private final Duration unloadingTime;

	/** the cost for loading and storing a SKU at the origin location. */
	private final Money loadingCost;

	/** the cost for unloading and storing a SKU at the destination location. */
	private final Money unloadingCost;

	/** the cost to transport one SKU per km. */
	private final Money transportCostPerKm;

	/**
	 * Create a TransportRealizationStep, fixing SKU, durations and costs for one
	 * step in a trip.
	 * 
	 * @param id                 String; the identifier for this TransportStep
	 * @param origin             Actor; the actor at the origin (company, port,
	 *                           terminal)
	 * @param destination        Actor; the actor at the destination (company, port,
	 *                           terminal)
	 * @param transportMode      TransportMode; the transport mode between origin
	 *                           and destination
	 * @param sku                Sku; the SKU that is used for transprort
	 * @param loadingTime        Duration; the time to load a SKU at the origin
	 *                           (including typical waiting times)
	 * @param unloadingTime      Duration; the time to unload a SKU at the
	 *                           destination (including typical waiting times)
	 * @param loadingCost        Money; the cost for loading and storing a SKU at
	 *                           the origin location
	 * @param unloadingCost      Money; the cost for unloading and storing a SKU at
	 *                           the destination location
	 * @param transportCostPerKm Money; the cost to transport one SKU per km
	 */
	@SuppressWarnings("checkstyle:parameternumber")
	public TransportRealizationStep(final String id, final Actor origin, final Actor destination,
			final TransportMode transportMode, final Sku sku, final Duration loadingTime, final Duration unloadingTime,
			final Money loadingCost, final Money unloadingCost, final Money transportCostPerKm) {
		Throw.whenNull(id, "id cannot be null");
		Throw.whenNull(origin, "origin cannot be null");
		Throw.whenNull(destination, "destination cannot be null");
		Throw.whenNull(transportMode, "transportMode cannot be null");
		Throw.whenNull(sku, "sku cannot be null");
		Throw.whenNull(loadingTime, "loadingTime cannot be null");
		Throw.whenNull(unloadingTime, "unloadingTime cannot be null");
		Throw.whenNull(loadingCost, "loadingCost cannot be null");
		Throw.whenNull(unloadingCost, "unloadingCost cannot be null");
		Throw.whenNull(transportCostPerKm, "transportCostPerKm cannot be null");
		this.id = id;
		this.origin = origin;
		this.destination = destination;
		this.transportMode = transportMode;
		this.sku = sku;
		this.loadingTime = loadingTime;
		this.unloadingTime = unloadingTime;
		this.loadingCost = loadingCost;
		this.unloadingCost = unloadingCost;
		this.transportCostPerKm = transportCostPerKm;
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * Return the actor at the origin (company, port, terminal).
	 * 
	 * @return origin Actor; the actor at the origin (company, port, terminal)
	 */
	public Actor getOrigin() {
		return this.origin;
	}

	/**
	 * Return the actor at the destination (company, port, terminal).
	 * 
	 * @return destination Actor; the actor at the destination (company, port,
	 *         terminal)
	 */
	public Actor getDestination() {
		return this.destination;
	}

	/**
	 * Return the transport mode between origin and destination.
	 * 
	 * @return TransportMode; the transport mode between origin and destination
	 */
	public TransportMode getTransportMode() {
		return this.transportMode;
	}

	/**
	 * Return the SKU that is used in this transport realization.
	 * 
	 * @return Sku; the SKU that is used in this transport realization
	 */
	public Sku getSku() {
		return this.sku;
	}

	/**
	 * Return the time to load one SKU at the origin (including typical waiting
	 * times).
	 * 
	 * @return Duration; the time to load one SKU at the origin (including typical
	 *         waiting times)
	 */
	public Duration getLoadingTime() {
		return this.loadingTime;
	}

	/**
	 * Return the time to unload one SKU at the destination (including typical
	 * waiting times).
	 * 
	 * @return Duration; the time to unload one SKU at the destination (including
	 *         typical waiting times)
	 */
	public Duration getEstimatedUnloadingTime() {
		return this.unloadingTime;
	}

	/**
	 * Return the costs for loading and storing the one SKU at the origin location.
	 * 
	 * @return Money; the costs for loading and storing the one SKU at the origin
	 *         location
	 */
	public Money getEstimatedLoadingCost() {
		return this.loadingCost;
	}

	/**
	 * Return the costs for loading and storing the one SKU at the destination
	 * location.
	 * 
	 * @return Money; the costs for unloading and storing the one SKU at the
	 *         destination location
	 */
	public Money getEstimatedUnloadingCost() {
		return this.unloadingCost;
	}

	/**
	 * Return the transport cost for the SKU per km, for the TransportStep's
	 * transport mode.
	 * 
	 * @return Money; the transport cost for the SKU per km, for the TransportStep's
	 *         transport mode
	 */
	public Money getEstimatedTransportCostPerKm() {
		return this.transportCostPerKm;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return Objects.hash(this.destination, this.id, this.loadingCost, this.loadingTime, this.origin, this.sku,
				this.transportCostPerKm, this.transportMode, this.unloadingCost, this.unloadingTime);
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("checkstyle:needbraces")
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransportRealizationStep other = (TransportRealizationStep) obj;
		return Objects.equals(this.destination, other.destination) && Objects.equals(this.id, other.id)
				&& Objects.equals(this.loadingCost, other.loadingCost)
				&& Objects.equals(this.loadingTime, other.loadingTime) && Objects.equals(this.origin, other.origin)
				&& Objects.equals(this.sku, other.sku)
				&& Objects.equals(this.transportCostPerKm, other.transportCostPerKm)
				&& Objects.equals(this.transportMode, other.transportMode)
				&& Objects.equals(this.unloadingCost, other.unloadingCost)
				&& Objects.equals(this.unloadingTime, other.unloadingTime);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "TransportRealizationStep [id=" + this.id + ", origin=" + this.origin + ", destination="
				+ this.destination + ", transportMode=" + this.transportMode + ", sku=" + this.sku + ", loadingTime="
				+ this.loadingTime + ", unloadingTime=" + this.unloadingTime + ", loadingCost=" + this.loadingCost
				+ ", unloadingCost=" + this.unloadingCost + ", transportCostPerKm=" + this.transportCostPerKm + "]";
	}

}
