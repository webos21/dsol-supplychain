package nl.tudelft.simulation.supplychain.messaging.product;

import java.io.Serializable;

import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.actor.messaging.devices.components.ReceivingDeviceInterface;
import nl.tudelft.simulation.supplychain.actor.messaging.devices.components.SendingDevice;
import nl.tudelft.simulation.supplychain.actor.messaging.devices.types.DeviceType;

/**
 * A reference implementation of a product sender (e.g., a crossdock or a warehouse). <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ProductSender extends SendingDevice
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the delay to send. */
    private final DistContinuousDuration delayDist;

    /** simulator. */
    private final SCSimulatorInterface simulator;

    /**
     * Constructs a new EmailApplication.
     * @param name the name of the email application
     * @param simulator the simulator to use
     * @param delayDist the delay to send
     */
    public ProductSender(final String name, final SCSimulatorInterface simulator,
            final DistContinuousDuration delayDist)
    {
        super(name, DeviceType.OTHER);
        this.simulator = simulator;
        this.delayDist = delayDist;
    }

    /** {@inheritDoc} */
    @Override
    public Serializable send(final Message message)
    {
        ReceivingDeviceInterface receiver = message.getReceiver().getReceivingDevices(this.getDeviceType())[0];
        try
        {
            this.simulator.scheduleEventRel(this.delayDist.draw(), this, receiver, "receive", new Serializable[] { message });
        }
        catch (Exception exception)
        {
            Logger.warn(exception, "send");
        }
        return Boolean.TRUE;
    }
}
