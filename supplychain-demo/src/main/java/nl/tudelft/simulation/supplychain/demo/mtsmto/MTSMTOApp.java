package nl.tudelft.simulation.supplychain.demo.mtsmto;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.logger.CategoryLogger;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.swing.gui.ConsoleLogger;
import nl.tudelft.simulation.dsol.swing.gui.ConsoleOutput;
import nl.tudelft.simulation.dsol.swing.gui.DsolPanel;
import nl.tudelft.simulation.dsol.swing.gui.animation.DsolAnimationApplication;
import nl.tudelft.simulation.language.DsolException;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainAnimator;
import nl.tudelft.simulation.supplychain.gui.SCControlPanel;

/**
 * TestModelApp.java. <br>
 * <br>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the
 * Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MTSMTOApp extends DsolAnimationApplication {
	/** */
	private static final long serialVersionUID = 20221201L;

	/**
	 * @param title
	 * @param panel
	 * @throws DSOLException
	 * @throws IllegalArgumentException
	 * @throws RemoteException
	 */
	public MTSMTOApp(final String title, final DsolPanel panel)
			throws RemoteException, IllegalArgumentException, DsolException {
		super(panel, title, new Bounds2d(-400, 400, -300, 300));
		panel.enableSimulationControlButtons();
		panel.getTabbedPane().setSelectedIndex(0);
	}

	/**
	 * @param args args
	 * @throws RemoteException     if error
	 * @throws SimRuntimeException if error
	 * @throws NamingException     if error
	 * @throws DSOLException       on dsol error
	 */
	public static void main(final String[] args)
			throws SimRuntimeException, NamingException, RemoteException, DsolException {
		CategoryLogger.setAllLogLevel(Level.WARNING);
		CategoryLogger.setAllLogMessageFormat("{level} - {class_name}.{method}:{line}  {message}");

		SupplyChainAnimator animator = new SupplyChainAnimator("MTSMTO", Time.ZERO);
		animator.setSpeedFactor(3600.0);
		MTSMTOModel model = new MTSMTOModel(animator);
		SingleReplication<Duration> replication = new SingleReplication<Duration>("rep1", Duration.ZERO, Duration.ZERO,
				new Duration(3000.0, DurationUnit.HOUR));
		animator.initialize(model, replication);
		DsolPanel panel = new DsolPanel(new SCControlPanel(model, animator));
		panel.addTab("logger", new ConsoleLogger(Level.INFO));
		panel.addTab("console", new ConsoleOutput());
		new MTSMTOApp("MTSMTO", panel);
	}

}
