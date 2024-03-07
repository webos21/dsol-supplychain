package nl.tudelft.simulation.supplychain.test;

import java.io.Serializable;
import java.net.URL;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.MassUnit;
import org.djunits.unit.VolumeUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Mass;
import org.djunits.value.vdouble.scalar.Volume;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.draw.point.Point;

import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.supplychain.animation.ContentAnimator;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainAnimator;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModel;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;
import nl.tudelft.simulation.supplychain.message.store.trade.LeanTradeMessageStore;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.Sku;
import nl.tudelft.simulation.supplychain.reference.BankActor;
import nl.tudelft.simulation.supplychain.reference.Customer;
import nl.tudelft.simulation.supplychain.role.banking.BankingActor;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;

/**
 * The TestModel for the supplychain package.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the
 * Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TestModel extends SupplyChainModel {
	/** the serial version uid. */
	private static final long serialVersionUID = 20221201L;

	/** timing run-time. */
	private long startTimeMs = 0;

	/** the simulator. */
	private SupplyChainSimulatorInterface devsSimulator;

	/** */
	Product laptop;

	/** */
	Factory factory;

	/** */
	PCShop pcShop;

	/** */
	Client client;

	/**
	 * constructs a new TestModel.
	 * 
	 * @param simulator the simulator
	 */
	public TestModel(final SupplyChainAnimator simulator) {
		super(simulator);
		// We don't do anything to prevent state-based replications.
	}

	/** {@inheritDoc} */
	@Override
	public void constructModel() {
		try {
			this.startTimeMs = System.currentTimeMillis();
			this.devsSimulator = (SupplyChainSimulatorInterface) this.simulator;
			if (this.devsSimulator instanceof AnimatorInterface) {
				// First we create some background. We set the zValue to -Double.Min value to
				// ensure that it is actually drawn
				// "below" our actors and messages.
				OrientedPoint3d op3d = new OrientedPoint3d(0.0, 0.0, -Double.MIN_VALUE);
				Bounds3d b3d = new Bounds3d(1618, 716, 0);
				URL wmGif = TestModel.class.getResource("/supplychain/images/worldmap.gif");
				new SingleImageRenderable<>(op3d, b3d, this.devsSimulator, wmGif);
			}

			FinancingActor bankFinActor = new Customer("BankCustomer", "BankCustomer", this, new OrientedPoint2d(0, 0),
					"ING", new LeanTradeMessageStore(this.devsSimulator));
			BankingActor bankActor = new BankActor("BankActor", "BankActor", this, new OrientedPoint2d(0, 0), "ING",
					new LeanTradeMessageStore(this.devsSimulator));
			Money bankMoney = new Money(0.0, MoneyUnit.USD);

			// create the bank
			BankAccount ing = new BankAccount(bankFinActor, bankActor, bankMoney);

			// create a product
			this.laptop = new Product(this, "Laptop", Sku.PIECE, new Money(1400.0, MoneyUnit.USD),
					new Mass(6.5, MassUnit.KILOGRAM), new Volume(0.05, VolumeUnit.CUBIC_METER), 0.0);

			// create a manufacturer
			this.factory = new Factory("Factory", "Factory", this, new OrientedPoint2d(200, 200), "", ing,
					new Money(50000.0, MoneyUnit.USD), new LeanTradeMessageStore(this.devsSimulator), this.laptop,
					1000);

			// create a retailer
			this.pcShop = new PCShop("PCshop", "PCshop", this, new OrientedPoint2d(20, 200), "", ing,
					new Money(50000.0, MoneyUnit.USD), new LeanTradeMessageStore(this.devsSimulator), this.laptop, 10,
					this.factory);

			// create a customer
			this.client = new Client("Client", "Client", this, new OrientedPoint2d(100, 100), "", ing,
					new Money(1500000.0, MoneyUnit.USD), new LeanTradeMessageStore(this.devsSimulator), this.laptop,
					this.pcShop);

			// schedule a remark that the simulation is ready
			Duration endTime = new Duration(this.simulator.getReplication().getRunLength().doubleValue() - 0.001,
					DurationUnit.SI);
			this.devsSimulator.scheduleEventRel(endTime, this, "endSimulation", new Serializable[] {});

			// Create the animation.
			ContentAnimator contentAnimator = new ContentAnimator(this.devsSimulator);
			contentAnimator.subscribe(this.factory);
			contentAnimator.subscribe(this.pcShop);
			contentAnimator.subscribe(this.client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * end of simulation -- display a message.
	 */
	protected void endSimulation() {
		System.err.println("End of TestModel replication");
		System.err.println("Runtime = " + ((System.currentTimeMillis() - this.startTimeMs) / 1000) + " seconds.");
		System.err.println("Simulation time = " + this.devsSimulator.getSimulatorTime());
	}

	/** {@inheritDoc} */
	@Override
	public Length calculateDistance(final Point<?> loc1, final Point<?> loc2) {
		double dx = loc2.getX() - loc1.getX();
		double dy = loc2.getY() - loc1.getY();
		return Length.instantiateSI(Math.sqrt(dx * dx + dy * dy));
	}

}
