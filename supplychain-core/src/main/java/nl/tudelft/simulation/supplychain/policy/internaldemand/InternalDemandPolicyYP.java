package nl.tudelft.simulation.supplychain.policy.internaldemand;

import org.djunits.value.vdouble.scalar.Length;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.inventory.Inventory;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.message.trade.YellowPageRequest;
import nl.tudelft.simulation.supplychain.role.yellowpage.YellowPageActor;

/**
 * The InternalDemandPolicyYP is a simple implementation of the business logic
 * to handle a request for new products through a yellow page request. When
 * receiving the internal demand, it just creates an YP request, without a given
 * time delay.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the
 * Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InternalDemandPolicyYP extends InternalDemandPolicy {
	/** the serial version uid. */
	private static final long serialVersionUID = 20221201L;

	/** the yellow page actor to use. */
	private Actor yp;

	/** maximum distance to use in the search. */
	private Length maximumDistance;

	/** maximum number of actors to return. */
	private int maximumNumber;

	/**
	 * Constructs a new InternalDemandPolicyYP.
	 * 
	 * @param owner           the owner of the internal demand
	 * @param handlingTime    the handling time distribution delay to use
	 * @param yp              the Actor that provides the yp service
	 * @param maximumDistance the search distance to use for all products
	 * @param maximumNumber   the max number of suppliers to return
	 * @param stock           the stock for being able to change the ordered amount
	 */
	public InternalDemandPolicyYP(final Role owner, final DistContinuousDuration handlingTime, final Actor yp,
			final Length maximumDistance, final int maximumNumber, final Inventory stock) {
		super("InternalDemandPolicyYP", owner, handlingTime, stock);
		this.yp = yp;
		this.maximumDistance = maximumDistance;
		this.maximumNumber = maximumNumber;
	}

	/** {@inheritDoc} */
	@Override
	public boolean handleMessage(final InternalDemand internalDemand) {
		if (!isValidMessage(internalDemand)) {
			return false;
		}
		if (super.inventory != null) {
			super.inventory.changeOrderedAmount(internalDemand.getProduct(), internalDemand.getAmount());
		}
		// create a YellowPageRequest
		YellowPageRequest ypRequest = new YellowPageRequest(getActor(), (YellowPageActor) this.yp,
				internalDemand.getUniqueId(), internalDemand.getProduct(), this.maximumDistance, this.maximumNumber);
		// and send it out immediately
		sendMessage(ypRequest, this.handlingTime.draw());
		return true;
	}
}
