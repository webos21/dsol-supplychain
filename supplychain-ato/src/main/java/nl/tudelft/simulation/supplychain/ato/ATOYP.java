package nl.tudelft.simulation.supplychain.ato;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.Point3d;

import nl.tudelft.simulation.actor.messagehandlers.HandleAllMessages;
import nl.tudelft.simulation.actor.messagehandlers.MessageHandlerInterface;
import nl.tudelft.simulation.actor.messaging.devices.reference.WebApplication;
import nl.tudelft.simulation.actor.unit.dist.DistConstantDuration;
import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.content.YellowPageRequest;
import nl.tudelft.simulation.supplychain.contentstore.memory.EmptyContentStore;
import nl.tudelft.simulation.supplychain.policy.yp.YellowPageRequestPolicy;
import nl.tudelft.simulation.supplychain.reference.YellowPage;

/**
 * <p>
 * Copyright (c) 2016 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="http://opentrafficsim.org/docs/license.html">OpenTrafficSim License</a>.
 * </p>
 * $LastChangedDate: 2015-07-24 02:58:59 +0200 (Fri, 24 Jul 2015) $, @version $Revision: 1147 $, by $Author: averbraeck $,
 * initial version Oct 12, 2018 <br>
 * @author <a href="http://www.tbm.tudelft.nl/averbraeck">Alexander Verbraeck</a> 
 * @author <a href="http://https://www.tudelft.nl/tbm/over-de-faculteit/afdelingen/multi-actor-systems/people/phd-candidates/b-bahareh-zohoori/">Bahareh Zohoori</a> 
 */
public class ATOYP extends YellowPage
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param name
     * @param simulator
     * @param position
     * @param bank
     */
    public ATOYP(String name, SCSimulatorInterface simulator, OrientedPoint3d position, Bank bank)
    {
        super(name, simulator, position, bank, new EmptyContentStore());

        // COMMUNICATION

        WebApplication www = new WebApplication("Web-" + name, this.simulator);
        super.addSendingDevice(www);
        MessageHandlerInterface webSystem = new HandleAllMessages(this);
        super.addReceivingDevice(www, webSystem, new DistConstantDuration(new Duration(10.0, DurationUnit.SECOND)));

        // YP MESSAGE HANDLING

        addContentHandler(YellowPageRequest.class, new YellowPageRequestPolicy(this, new Duration(10.0, DurationUnit.MINUTE)));

        // ANIMATION

        if (simulator instanceof AnimatorInterface)
        {
            try
            {
                new SingleImageRenderable<>(this, simulator,
                        ATOYP.class.getResource("/nl/tudelft/simulation/supplychain/images/YellowPage.gif"));
            }
            catch (RemoteException | NamingException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Bounds3d getBounds()
    {
        return new Bounds3d(25.0, 25.0, 1.0);
    }

}
