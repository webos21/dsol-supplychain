package nl.tudelft.simulation.supplychain.transport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;
import org.djutils.immutablecollections.ImmutableArrayList;
import org.djutils.immutablecollections.ImmutableList;

/**
 * TransportRealization describes a confirmed way to get goods from A to B. The
 * class can incicate a singular transport mode that transports the goods from A
 * to B, e.g., trucking, or a multimodal option that involves, e.g., a truck to
 * the Port, a containrship to another port, and trucking to the final
 * destination. Each of the modes has a different speed, and each of the
 * transfers will take time (and possibly cost money),
 * <p>
 * Copyright (c) 2022-2023 Delft University of Technology, Delft, the
 * Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportRealization implements Identifiable, Serializable {
	/** */
	private static final long serialVersionUID = 20221202L;

	/** the id of the TransportRealization. */
	private final String id;

	/** the sequence of TransportSteps. */
	private ImmutableList<TransportRealizationStep> transportSteps = new ImmutableArrayList<>(new ArrayList<>());

	/**
	 * Create a TransportRealization, fixing SKU, durations and costs for an entire
	 * trip.
	 * 
	 * @param id String; the id of the TransportRealization
	 */
	public TransportRealization(final String id) {
		Throw.whenNull(id, "id cannot be null");
		this.id = id;
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * Return the transport steps.
	 * 
	 * @return ImmutableList&lt;TransportRealizationStep&gt;; the transport steps
	 */
	public ImmutableList<TransportRealizationStep> getTransportSteps() {
		return this.transportSteps;
	}

	/**
	 * Add a transport step.
	 * 
	 * @param transportRealizationStep TransportRealizationStep; the new transport
	 *                                 step
	 */
	public void addTransportStep(final TransportRealizationStep transportRealizationStep) {
		Throw.whenNull(transportRealizationStep, "transportRealizationStep cannot be null");
		List<TransportRealizationStep> steps = this.transportSteps.toList();
		steps.add(transportRealizationStep);
		this.transportSteps = new ImmutableArrayList<>(steps);
	}

	/**
	 * Add a number of transport steps.
	 * 
	 * @param steps List&lt;TransportRealizationStep&gt;; the new transport steps
	 */
	public void addTransportSteps(final List<TransportRealizationStep> steps) {
		Throw.whenNull(steps, "steps cannot be null");
		for (TransportRealizationStep transportRealizationStep : steps) {
			addTransportStep(transportRealizationStep);
		}
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.transportSteps);
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
		TransportRealization other = (TransportRealization) obj;
		return Objects.equals(this.id, other.id) && Objects.equals(this.transportSteps, other.transportSteps);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "TransportRealization [id=" + this.id + ", transportSteps=" + this.transportSteps + "]";
	}

}
