package nl.tudelft.simulation.actor.messaging.devices.reference;

import org.djunits.unit.DurationUnit;

import nl.tudelft.simulation.actor.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.actor.messaging.comparators.FiFo;
import nl.tudelft.simulation.actor.messaging.devices.components.DelaySendingDevice;
import nl.tudelft.simulation.actor.messaging.devices.components.ReceivingDevice;
import nl.tudelft.simulation.actor.messaging.devices.components.SendingReceivingDevice;
import nl.tudelft.simulation.actor.messaging.devices.types.DeviceType;
import nl.tudelft.simulation.actor.messaging.queues.MessageQueue;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;

/**
 * A reference implementation of a web application. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class WebApplication extends SendingReceivingDevice
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * Constructs a new EmailApplication.
     * @param name the name of the email application
     * @param simulator the simulator to use
     */
    public WebApplication(final String name, final SCSimulatorInterface simulator)
    {
        super(name, new ReceivingDevice(name + "-R", DeviceType.NETWORK, new MessageQueue(new FiFo())),
                new DelaySendingDevice(name + "-S", DeviceType.NETWORK, simulator, new DistContinuousDuration(
                        new DistTriangular(simulator.getModel().getStream("default"), 1.0, 2.0, 5.0), DurationUnit.SECOND)));
    }
}
