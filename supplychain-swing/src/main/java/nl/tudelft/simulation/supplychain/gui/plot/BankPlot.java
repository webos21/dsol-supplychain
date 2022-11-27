package nl.tudelft.simulation.supplychain.gui.plot;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.event.EventInterface;
import org.djutils.event.EventListenerInterface;
import org.djutils.event.EventProducer;
import org.djutils.event.TimedEvent;
import org.djutils.event.TimedEventType;

import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.banking.BankAccount;
import nl.tudelft.simulation.supplychain.finance.Money;

/**
 * StockPlot.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class BankPlot extends XYChart
{
    /** */
    private static final long serialVersionUID = 20200211L;
    
    /** */
    private SimPersistent<Duration> balancePersistent;

    /**
     * @param simulator
     * @param title
     * @param bankAccount
     */
    public BankPlot(final SCSimulatorInterface simulator, final String title, final BankAccount bankAccount)
    {
        super(simulator, title);
        BalanceListener balanceListener = new BalanceListener(simulator, bankAccount);
        try
        {
            this.balancePersistent = new SimPersistent<>("balance " + title, simulator, balanceListener,
                BalanceListener.BALANCE_CHANGE_EVENT);
            add("balance", this.balancePersistent, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT);
        }
        catch (RemoteException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * LalanceListener - delegate class to handle the bank account balance change subscription and event production for the
     * Persistent variables. <br>
     * <br>
     * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>.
     * The source code and binary code of this software is proprietary information of Delft University of Technology.
     * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
     */
    private static class BalanceListener extends EventProducer implements EventListenerInterface
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** the simulator to get the time for the TimedEvent. */
        private final SCSimulatorInterface simulator;

        /** An event to indicate stock levels changed */
        static final TimedEventType BALANCE_CHANGE_EVENT = new TimedEventType("BALANCE_CHANGE_EVENT");

        /**
         * @param simulator
         * @param bankAccount
         */
        public BalanceListener(final SCSimulatorInterface simulator, final BankAccount bankAccount)
        {
            super();
            this.simulator = simulator;
            bankAccount.addListener(this, BankAccount.BANK_ACCOUNT_CHANGED_EVENT);
        }

        /** {@inheritDoc} */
        @Override
        public void notify(final EventInterface event) throws RemoteException
        {
            Money balance = (Money) event.getContent();
            fireEvent(new TimedEvent<Double>(BALANCE_CHANGE_EVENT, this, balance.getAmount(), this.simulator
                .getSimulatorTime().si));
        }

        /** {@inheritDoc} */
        @Override
        public Serializable getSourceId()
        {
            return "BalanceListener";
        }

    }
}
