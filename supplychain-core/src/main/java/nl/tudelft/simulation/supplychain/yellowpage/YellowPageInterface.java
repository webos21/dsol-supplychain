package nl.tudelft.simulation.supplychain.yellowpage;

import java.util.List;

import nl.tudelft.simulation.supplychain.actor.Actor;

/**
 * A YellowPageInterface, which enables finding of actors based on a registry in which actors can register themselves. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface YellowPageInterface
{
    /**
     * finds actors based on the regex.
     * @param regex the name of the actor as regular expression
     * @return ActorInterface[] the result
     */
    List<Actor> findActor(String regex);

    /**
     * finds an actor based on the regex.
     * @param regex the name of the actor as regular expression
     * @param topic the topic for which this actor is registered
     * @return ActorInterface[] the result
     */
    List<Actor> findActor(String regex, Topic topic);

    /**
     * finds an actor based on the category.
     * @param topic the category for this actor
     * @return ActorInterface[] the result
     */
    List<Actor> findActor(Topic topic);

    /**
     * registers an actor.
     * @param actor the actor
     * @param topic the category
     * @return success
     */
    boolean register(Actor actor, Topic topic);
}
