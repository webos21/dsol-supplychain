package nl.tudelft.simulation.supplychain.test;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.logger.CategoryLogger;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.actor.dsol.SCAnimator;
import nl.tudelft.simulation.actor.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.swing.gui.ConsoleLogger;
import nl.tudelft.simulation.dsol.swing.gui.ConsoleOutput;
import nl.tudelft.simulation.dsol.swing.gui.DSOLPanel;
import nl.tudelft.simulation.dsol.swing.gui.TablePanel;
import nl.tudelft.simulation.dsol.swing.gui.animation.DSOLAnimationApplication;
import nl.tudelft.simulation.language.DSOLException;
import nl.tudelft.simulation.supplychain.gui.SCControlPanel;
import nl.tudelft.simulation.supplychain.gui.plot.BankPlot;
import nl.tudelft.simulation.supplychain.gui.plot.StockPlot;

/**
 * TestModelApp.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class TestModelApp extends DSOLAnimationApplication
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the model. */
    private final TestModel model;

    /**
     * @param title
     * @param panel
     * @param model
     * @throws DSOLException
     * @throws IllegalArgumentException
     * @throws RemoteException
     */
    public TestModelApp(final String title, final DSOLPanel panel, final TestModel model)
            throws RemoteException, IllegalArgumentException, DSOLException
    {
        super(panel, title, new Bounds2d(-100, 300, 50, 250));
        this.model = model;
        panel.enableSimulationControlButtons();
        addTabs();
    }

    private void addTabs()
    {
        TablePanel charts = new TablePanel(3, 2);
        getDSOLPanel().addTab("statistics", charts);
        getDSOLPanel().getTabbedPane().setSelectedIndex(1);
        SCSimulatorInterface devsSimulator = this.model.getSimulator();

        BankPlot fb = new BankPlot(devsSimulator, "Factory Bank balance", this.model.factory.getBankAccount());
        charts.setCell(fb.getSwingPanel(), 0, 0);

        BankPlot pb = new BankPlot(devsSimulator, "PCShop Bank balance", this.model.pcShop.getBankAccount());
        charts.setCell(pb.getSwingPanel(), 1, 0);

        BankPlot cb = new BankPlot(devsSimulator, "Client Bank balance", this.model.client.getBankAccount());
        charts.setCell(cb.getSwingPanel(), 2, 0);

        StockPlot fs = new StockPlot(devsSimulator, "Factory stock Laptop", this.model.factory.getStock(), this.model.laptop);
        charts.setCell(fs.getSwingPanel(), 0, 1);

        StockPlot ps = new StockPlot(devsSimulator, "PCShop stock Laptop", this.model.pcShop.getStock(), this.model.laptop);
        charts.setCell(ps.getSwingPanel(), 1, 1);
    }

    /**
     * @param args args
     * @throws RemoteException if error
     * @throws SimRuntimeException if error
     * @throws NamingException if error
     * @throws DSOLException on dsol error
     */
    public static void main(final String[] args) throws SimRuntimeException, NamingException, RemoteException, DSOLException
    {
        CategoryLogger.setAllLogLevel(Level.INFO);
        CategoryLogger.setAllLogMessageFormat("{level} - {class_name}.{method}:{line}  {message}");

        SCAnimator animator = new SCAnimator("MTSMTO", Time.ZERO);
        TestModel model = new TestModel(animator);
        ReplicationInterface<Duration> replication =
                new SingleReplication<Duration>("rep1", Duration.ZERO, Duration.ZERO, new Duration(1800.0, DurationUnit.HOUR));
        animator.initialize(model, replication);
        DSOLPanel panel = new DSOLPanel(new SCControlPanel(model, animator));
        panel.addTab("logger", new ConsoleLogger(Level.INFO));
        panel.addTab("console", new ConsoleOutput());
        new TestModelApp("TestModelApp", panel, model);
    }

}
