package nl.tudelft.simulation.supplychain.test;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint2d;

import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.inventory.Inventory;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.policy.order.OrderPolicy;
import nl.tudelft.simulation.supplychain.policy.order.OrderPolicyStock;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicy;
import nl.tudelft.simulation.supplychain.policy.rfq.RequestForQuotePolicy;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Supplier;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;
import nl.tudelft.simulation.supplychain.role.selling.SellingRoleRFQ;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

/**
 * The ComputerShop named Factory.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the
 * Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Factory extends Supplier {
	/** the serial version uid. */
	private static final long serialVersionUID = 20221201L;

	private BankAccount bankAccount;

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
	 * @param product             initial stock product
	 * @param amount              amount of initial stock
	 * @throws ActorAlreadyDefinedException when the actor was already registered in
	 *                                      the model
	 * @throws NamingException              on animation error
	 * @throws RemoteException              on animation error
	 */
	@SuppressWarnings("checkstyle:parameternumber")
	public Factory(final String id, final String name, final SupplyChainModelInterface model,
			final OrientedPoint2d location, final String locationDescription, final BankAccount bank,
			final Money initialBalance, final TradeMessageStoreInterface messageStore, final Product product,
			final double amount) throws ActorAlreadyDefinedException, RemoteException, NamingException {
		super(id, name, model, location, locationDescription, bank, initialBalance, messageStore);
		this.bankAccount = bank;
		// give the retailer some stock
		getInventory().addToInventory(product, amount, product.getUnitMarketPrice().multiplyBy(amount));
		// We initialize Factory
		this.init();
		// Let's give Factory its corresponding image
		if (getSimulator() instanceof AnimatorInterface) {
			new SingleImageRenderable<>(this, getSimulator(),
					Factory.class.getResource("/nl/tudelft/simulation/supplychain/images/Manufacturer.gif"));
		}
	}

	/**
	 * @throws RemoteException simulator error
	 */
	public void init() throws RemoteException {
		// tell Factory to use the RFQPolicy to handle RFQs
//		RequestForQuotePolicy rfqPolicy = new RequestForQuotePolicy(this, getInventory(), 1.2,
//				new DistConstantDuration(new Duration(1.23, DurationUnit.HOUR)), TransportMode.PLANE);
		RequestForQuotePolicy rfqPolicy = new RequestForQuotePolicy(this.getInventoryRole(), getInventory(), 1.2,
				new DistConstantDuration(new Duration(1.23, DurationUnit.HOUR)), new Duration(1.23, DurationUnit.HOUR));
		//
		// create an order Policy
		OrderPolicy orderPolicy = new OrderPolicyStock(this.getInventoryRole(), getInventory());
		//
		// hopefully, Factory will get payments in the end
		PaymentPolicy paymentPolicy = new PaymentPolicy(this.getInventoryRole(), getBankAccount());
		//
		// add the Policys to the SellingRole
		SellingRole sellingRole = new SellingRoleRFQ((SellingActor) this.getSellingRole().getActor(), rfqPolicy,
				orderPolicy, paymentPolicy);
		super.setSellingRole(sellingRole);
		//
		// CHARTS
		//
		if (getSimulator() instanceof AnimatorInterface) {
			XYChart bankChart = new XYChart(getSimulator(), "BankAccount " + getName());
			bankChart.add("bank account", getBankAccount(), BankAccount.BANK_ACCOUNT_CHANGED_EVENT);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Bounds3d getBounds() {
		return new Bounds3d(25.0, 25.0, 1.0);
	}

	public Inventory getInventory() {
		return getInventoryRole().getInventory();
	}

	public BankAccount getBankAccount() {
		return this.bankAccount;
	}
}
