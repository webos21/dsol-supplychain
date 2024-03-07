package nl.tudelft.simulation.supplychain.demo.reference;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.supplychain.actor.messaging.devices.reference.WebApplication;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.message.receiver.MessageReceiver;
import nl.tudelft.simulation.supplychain.message.store.EmptyMessageStore;
import nl.tudelft.simulation.supplychain.message.trade.YellowPageRequest;
import nl.tudelft.simulation.supplychain.messagehandlers.HandleAllMessages;
import nl.tudelft.simulation.supplychain.policy.yellowpage.YellowPageRequestPolicy;
import nl.tudelft.simulation.supplychain.reference.YellowPage;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

/**
 * MtsMtoYP.java. <br>
 * <br>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the
 * Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DemoYP extends YellowPage {
	/** */
	private static final long serialVersionUID = 20221201L;

	/**
	 * @param name
	 * @param simulator
	 * @param position
	 * @param bank
	 */
	public DemoYP(String name, SupplyChainSimulatorInterface simulator, OrientedPoint3d position, Bank bank) {
		super(name, simulator, position, bank, new EmptyMessageStore());

		// COMMUNICATION

		WebApplication www = new WebApplication("Web-" + name, this.simulator);
		super.addSendingDevice(www);
		MessageReceiver webSystem = new HandleAllMessages(this);
		super.addReceivingDevice(www, webSystem, new DistConstantDuration(new Duration(10.0, DurationUnit.SECOND)));

		// YP MESSAGE HANDLING

		addContentHandler(YellowPageRequest.class,
				new YellowPageRequestPolicy(this, new Duration(10.0, DurationUnit.MINUTE)));

		// ANIMATION

		if (simulator instanceof AnimatorInterface) {
			try {
				new SingleImageRenderable<>(this, simulator,
						DemoYP.class.getResource("/nl/tudelft/simulation/supplychain/images/YellowPage.gif"));
			} catch (RemoteException | NamingException exception) {
				exception.printStackTrace();
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public Bounds3d getBounds() {
		return new Bounds3d(25.0, 25.0, 1.0);
	}

}
