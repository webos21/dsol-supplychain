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
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.supplychain.stock.StockUpdateData;

/**
 * StockPlot.java. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class StockPlot extends XYChart
{
    /** */
    private static final long serialVersionUID = 20200211L;

    /** */
    private SimPersistent<Duration> actualPersistent;

    /** */
    private SimPersistent<Duration> claimedPersistent;

    /** */
    private SimPersistent<Duration> orderedPersistent;

    /**
     * @param simulator
     * @param title
     * @param stock
     * @param product
     */
    @SuppressWarnings("static-access")
    public StockPlot(final SCSimulatorInterface simulator, final String title, final StockInterface stock,
            final Product product)
    {
        super(simulator, title);
        StockListener stockListener = new StockListener(simulator, stock, product);
        try
        {
            this.actualPersistent = new SimPersistent<>("actual stock " + title, simulator, stockListener,
                    StockListener.STOCK_ACTUAL_CHANGE_EVENT);
            this.claimedPersistent = new SimPersistent<>("claimed stock " + title, simulator, stockListener,
                    StockListener.STOCK_CLAIMED_CHANGE_EVENT);
            this.orderedPersistent = new SimPersistent<>("ordered stock " + title, simulator, stockListener,
                    StockListener.STOCK_ORDERED_CHANGE_EVENT);
            add("actual stock", this.actualPersistent, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT);
            add("claimed stock", this.claimedPersistent, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT);
            add("ordered stock", this.orderedPersistent, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT);
        }
        catch (RemoteException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * StockListener - delegate class to handle the stock change subscription, filtering for the right product, and event
     * production for the SimPersistent variables. <br>
     * <br>
     * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * <br>
     * The supply chain Java library uses a BSD-3 style license.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    private static class StockListener extends EventProducer implements EventListenerInterface
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** the product to filter the updates for. */
        private final Product product;

        /** the simulator to get the time for the TimedEvent. */
        private final SCSimulatorInterface simulator;

        /** An event to indicate stock levels changed */
        static final TimedEventType STOCK_ACTUAL_CHANGE_EVENT = new TimedEventType("STOCK_ACTUAL_CHANGE_EVENT");

        /** An event to indicate stock levels changed */
        static final TimedEventType STOCK_CLAIMED_CHANGE_EVENT = new TimedEventType("STOCK_CLAIMED_CHANGE_EVENT");

        /** An event to indicate stock levels changed */
        static final TimedEventType STOCK_ORDERED_CHANGE_EVENT = new TimedEventType("STOCK_ORDERED_CHANGE_EVENT");

        /**
         * @param simulator
         * @param stock
         * @param product
         */
        public StockListener(final SCSimulatorInterface simulator, final StockInterface stock, final Product product)
        {
            super();
            this.product = product;
            this.simulator = simulator;
            try
            {
                stock.addListener(this, StockInterface.STOCK_CHANGE_EVENT);
            }
            catch (RemoteException exception)
            {
                exception.printStackTrace();
            }
        }

        /** {@inheritDoc} */
        @Override
        public void notify(final EventInterface event) throws RemoteException
        {
            StockUpdateData data = (StockUpdateData) event.getContent();
            if (!data.getProductName().equals(this.product.getName()))
                return;
            fireEvent(new TimedEvent<Double>(STOCK_ACTUAL_CHANGE_EVENT, this, data.getActualAmount(),
                    this.simulator.getSimulatorTime().si));
            fireEvent(new TimedEvent<Double>(STOCK_CLAIMED_CHANGE_EVENT, this, data.getClaimedAmount(),
                    this.simulator.getSimulatorTime().si));
            fireEvent(new TimedEvent<Double>(STOCK_ORDERED_CHANGE_EVENT, this, data.getOrderedAmount(),
                    this.simulator.getSimulatorTime().si));
        }

        /** {@inheritDoc} */
        @Override
        public Serializable getSourceId()
        {
            return "StockListener";
        }

    }
}
