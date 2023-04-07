package nl.tudelft.simulation.supplychain.message;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;

import org.djunits.value.vdouble.scalar.Time;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import nl.tudelft.simulation.supplychain.actor.ActorInterface;
import nl.tudelft.simulation.supplychain.dsol.SCModelInterface;

/**
 * A message, which can be sent from a sender to a receiver. Extend this class to add content.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class Message implements Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221203L;

    /** sender of the message (necessary for a possible reply). */
    private final ActorInterface sender;

    /** the receiver of a message. */
    private final ActorInterface receiver;

    /** the timestamp of a message. */
    private final Time timestamp;

    /** the unqiue message id. */
    private final long uniqueId;

    /**
     * Construct a new message.
     * @param sender ActorInterface; the sender
     * @param receiver ActorInterface; the receiver
     */
    public Message(final SCModelInterface model, final ActorInterface sender, final ActorInterface receiver)
    {
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = sender.getSimulatorTime();
        this.uniqueId = sender.getSimulator().getUniqueMessageId();
    }
    
    /**
     * Construct a new message from JSON content.
     * @param json String; the message content encoded as a JSON string
     * @throws IOException when decoding of the message fails
     */
    public Message(final SCModelInterface model, final String json) throws IOException
    {
        StringReader in = new StringReader(json);
        JsonReader jr = new JsonReader(in);
        jr.beginObject();
        
        jr.endObject();
    }

    /**
     * Return the sender of the message (to allow for a reply to be sent).
     * @return ActorInterface; the sender of the message
     */
    public ActorInterface getSender()
    {
        return this.sender;
    }

    /**
     * Return the receiver of the message.
     * @return ActorInterface; the receiver of the message
     */
    public ActorInterface getReceiver()
    {
        return this.receiver;
    }

    /**
     * Return the timestamp of the message.
     * @return Time; the timestamp of the message
     */
    public Time getTimestamp()
    {
        return this.timestamp;
    }

    /**
     * Return the unique message id.
     * @return long; the unique message id.
     */
    public long getUniqueId()
    {
        return this.uniqueId;
    }

    /**
     * Return the content of the message as a JSON string.
     * @return String; the content of the message as a JSON string
     * @throws IOException when encoding of the message fails
     */
    protected String toJson() throws IOException
    {
        StringWriter out = new StringWriter();
        JsonWriter jw = new JsonWriter(out);
        jw.beginObject();
        jw.name("sender");
        jw.value(getSender().getName());
        jw.name("receiver");
        jw.value(getReceiver().getName());
        jw.name("uniqueId");
        jw.value(getUniqueId());
        jw.name("timestamp");
        jw.value(getTimestamp().getSI());
        encodeAsJson(jw);
        jw.endObject();
        jw.close();
        return out.toString();
    }

    /**
     * Write the further content of this message to a JSON string. The sender, receiver, unique id, and timestamp have already
     * been written.
     * @param jw JsonWriter; the JSON writer to use to encode the message
     * @throws IOException when encoding of the message fails
     */
    public abstract void encodeAsJson(JsonWriter jw) throws IOException;

    /**
     * Decode the further content of this message from a JSON string. The sender, receiver, unique id, and timestamp have already
     * been read.
     * @param jr JsonReader; the JSON reader to use to decode the message
     * @throws IOException when decoding of the message fails
     */
    public abstract void decodeFromJson(JsonReader jr) throws IOException;

}
