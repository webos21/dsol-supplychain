package nl.tudelft.simulation.supplychain.actor;

import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.bounds.Bounds;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.Point;
import org.djutils.immutablecollections.ImmutableSet;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.message.Message;

/**
 * The actor is the basic class in the nl.tudelft.simulation.actor package. It implements the behavior of a 'communicating'
 * object, that is able to exchange messages with other actors and process the incoming messages.<br>
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface ActorInterface extends PolicyHandlerInterface, Locatable
{

    /**
     * Add a role to the actor.
     * @param role Role; the role to add to the actor
     */
    void addRole(RoleInterface role);

    /**
     * Return the set of roles for this actor.
     * @return Set&lt;roles&gt;; the roles of this actor
     */
    ImmutableSet<RoleInterface> getRoles();

    /**
     * Receive a message from another actor, and handle it (storing or handling, depending on the MessageHandler).
     * @param message message; the message to receive
     */
    void receiveMessage(Message message);

    /**
     * Return the name of the actor.
     * @return String; the name of the actor
     */
    String getName();

    /**
     * Return the location description of the actor (e.g., a city, country).
     * @return String; the location description of the actor
     */
    String getLocationDescription();

    /**
     * Return the simulator to schedule simulation events on.
     * @return simulator SCSimulatorInterface the simulator
     */
    SCSimulatorInterface getSimulator();

    /**
     * Return the current simulation time.
     * @return Time; the current simulation time
     */
    default Time getSimulatorTime()
    {
        return getSimulator().getAbsSimulatorTime();
    }

    /** {@inheritDoc} */
    @Override
    Point<?> getLocation();

    /** {@inheritDoc} */
    @Override
    Bounds<?, ?, ?> getBounds();

    /**
     * Set the bounds of the object (size and relative height in the animation).
     * @param bounds the bounds for the (animation) object
     */
    void setBounds(Bounds3d bounds);

}
