package nl.tudelft.simulation.supplychain.actor;

/**
 * ActorNotFoundException to indicate that an actor could not be identified in the model that satisfies the given properties.
 * This could be an actor that could not be found based on a given id, or an actor that could not be found based on cost,
 * distance and/or service properties.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ActorNotFoundException extends Exception
{
    /** */
    private static final long serialVersionUID = 20230407L;

    /**
     * Create the exception with a message.
     * @param message String; the message to explain the exception
     */
    public ActorNotFoundException(final String message)
    {
        super(message);
    }

    /**
     * Wrap an earlier exception into this exception.
     * @param cause Throwable; the earlier exception to wrap
     */
    public ActorNotFoundException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * Wrap an earlier exception into this exception, and give it a description.
     * @param message String; the message to explain the exception
     * @param cause Throwable; the earlier exception to wrap
     */
    public ActorNotFoundException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

}
