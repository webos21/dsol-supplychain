package nl.tudelft.simulation.supplychain.reference;

import org.djutils.draw.point.OrientedPoint2d;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;

/**
 * Reference implementation for a DC.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the
 * Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DistributionCenter extends Retailer {
	/** the serial version uid. */
	private static final long serialVersionUID = 20221206L;

	/**
	 * @param id                  String, the unique id of the distribution center
	 * @param name                String; the longer name of the distributin center
	 * @param model               SupplyChainModelInterface; the model
	 * @param location            OrientedPoint2d; the location of the actor
	 * @param locationDescription String; the location description of the actor
	 *                            (e.g., a city, country)
	 * @param bank                Bank; the bank for the BankAccount
	 * @param initialBalance      Money; the initial balance for the actor
	 * @param messageStore        TradeMessageStoreInterface; the message store for
	 *                            messages
	 * @throws ActorAlreadyDefinedException when the actor was already registered in
	 *                                      the model
	 */
	@SuppressWarnings("checkstyle:parameternumber")
	public DistributionCenter(final String id, final String name, final SupplyChainModelInterface model,
			final OrientedPoint2d location, final String locationDescription, final BankAccount bank,
			final Money initialBalance, final TradeMessageStoreInterface messageStore)
			throws ActorAlreadyDefinedException {
		super(id, name, model, location, locationDescription, bank, initialBalance, messageStore);
	}

}
