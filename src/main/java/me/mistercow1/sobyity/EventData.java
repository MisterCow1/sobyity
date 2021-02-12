package me.mistercow1.sobyity;

import java.lang.reflect.Method;

public class EventData
{
    public final Object source;
    public final Method target;
    public final byte priority;

    /**
     * Data for event
     *
     * @param source source of the event
     * @param target target of the event
     * @param priority priority of the event
     *
     * @see Event
     *
     * @since 1.0.0
     */
    public EventData(Object source, Method target, byte priority)
    {
        this.source = source;
        this.target = target;
        this.priority = priority;
    }
}