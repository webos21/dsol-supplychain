package nl.tudelft.simulation.supplychain.reference;

import java.io.Serializable;

import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.role.inventory.InventoryRole;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;

/**
 * Reference implementation for a Supplier.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the
 * Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Supplier extends SupplyChainActor implements Serializable {
	/** the serial version uid. */
	private static final long serialVersionUID = 20221206L;

	/** The role to sell. */
	private SellingRole sellingRole = null;

	/** the role to keep inventory. */
	private InventoryRole inventoryRole = null;

	/**
	 * @param id                  String, the unique id of the supplier
	 * @param name                String; the longer name of the supplier
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
	public Supplier(final String id, final String name, final SupplyChainModelInterface model,
			final OrientedPoint2d location, final String locationDescription, final BankAccount bank,
			final Money initialBalance, final TradeMessageStoreInterface messageStore)
			throws ActorAlreadyDefinedException {
		super(id, name, model, location, locationDescription, bank, messageStore);
	}

	/**
	 * Return the selling role.
	 * 
	 * @return SellingRole; the selling role
	 */
	public SellingRole getSellingRole() {
		return this.sellingRole;
	}

	/**
	 * Set the selling role.
	 * 
	 * @param sellingRole SellingRole; the new selling role
	 */
	public void setSellingRole(final SellingRole sellingRole) {
		Throw.whenNull(sellingRole, "sellingRole cannot be null");
		Throw.when(this.sellingRole != null, IllegalStateException.class, "sellingRole already initialized");
		addRole(sellingRole);
		this.sellingRole = sellingRole;
	}

	/**
	 * Return the inventory role.
	 * 
	 * @return InventoryRole; the inventory role
	 */
	public InventoryRole getInventoryRole() {
		return this.inventoryRole;
	}

	/**
	 * Set the inventory role.
	 * 
	 * @param inventoryRole InventoryRole; the new inventory role
	 */
	public void setInventoryRole(final InventoryRole inventoryRole) {
		Throw.whenNull(inventoryRole, "inventoryRole cannot be null");
		Throw.when(this.inventoryRole != null, IllegalStateException.class, "inventoryRole already initialized");
		addRole(inventoryRole);
		this.inventoryRole = inventoryRole;
	}

	/** {@inheritDoc} */
	@Override
	public void receiveMessage(final Message message) {
		Throw.whenNull(this.sellingRole, "SellingRole not initialized for actor: " + this.getName());
		Throw.whenNull(this.inventoryRole, "InventoryRole not initialized for actor: " + this.getName());
		super.receiveMessage(message);
	}

	@Override
	public void checkNecessaryRoles() {
		// TODO Auto-generated method stub

	}

}
