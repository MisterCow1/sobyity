package me.mistercow1.sobyity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EventBus
{
    private static final Map<Class<? extends Event>, ArrayList<EventData>> REGISTRY_MAP = new HashMap<Class<? extends Event>, ArrayList<EventData>>();

    /**
     * Sorts event data in event class by priority
     *
     * @param clazz event class
     *
     * @see Event
     * @see EventData
     *
     * @since 1.0.0
     */
    private static void sortListValue(final Class<? extends Event> clazz)
    {
        final ArrayList<EventData> flexibleArray = new ArrayList<EventData>();

        for(final byte priority : EventPriority.EVENT_PRIORITIES)
        {
            for(EventData data : REGISTRY_MAP.get(clazz))
            {
                if(data.priority == priority)
                {
                    flexibleArray.add(data);
                }
            }
        }

        REGISTRY_MAP.put(clazz, flexibleArray);
    }

    /**
     * Returns a boolean to specify if a method is not an event handler
     *
     * @param method the method to check
     *
     * @return a boolean to specify if a method is not an event handler
     *
     * @since 1.0.0
     */
    private static boolean isMethodBad(final Method method)
    {
        return method.getParameterTypes().length != 1 || !method.isAnnotationPresent(EventHandler.class);
    }

    /**
     * Returns an ArrayList of EventData from an event class
     *
     * @param clazz the event class to get the EventData from
     *
     * @return an ArrayList of EventData from an event class
     *
     * @since 1.0.0
     */
    public static ArrayList<EventData> get(final Class<? extends Event> clazz)
    {
        return REGISTRY_MAP.get(clazz);
    }

    /**
     * Removes entries from the registry map
     *
     * @param removeOnlyEmptyValues to specify wherever ot not to remove only empty values
     *
     * @see #REGISTRY_MAP
     *
     * @since 1.0.0
     */
    public static void cleanMap(final boolean removeOnlyEmptyValues)
    {
        final Iterator<Map.Entry<Class<? extends Event>, ArrayList<EventData>>> iterator = REGISTRY_MAP.entrySet().iterator();

        while(iterator.hasNext())
        {
            if(!removeOnlyEmptyValues || iterator.next().getValue().isEmpty())
            {
                iterator.remove();
            }
        }
    }

    /**
     * Registers event listener class
     *
     * @param eventHandlerClass event listener class to register
     *
     * @see Event
     *
     * @since 1.0.0
     */
    public void register(Class<?> eventHandlerClass)
    {
        Object eventClassInstance = null;

        try
        {
            eventClassInstance = eventHandlerClass.newInstance();
        }
        catch(InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }

        for(final Method method : eventHandlerClass.getMethods())
        {
            if(!isMethodBad(method))
            {
                final Class<?> clazz = method.getParameterTypes()[0];

                final EventData data = new EventData(eventClassInstance, method, method.getAnnotation(EventHandler.class).priority());

                if(!data.target.isAccessible())
                {
                    data.target.setAccessible(true);
                }

                if(REGISTRY_MAP.containsKey(clazz))
                {
                    if(!REGISTRY_MAP.get(clazz).contains(data))
                    {
                        REGISTRY_MAP.get(clazz).add(data);
                        sortListValue((Class<? extends Event>) clazz);
                    }
                }
                else
                {
                    REGISTRY_MAP.put((Class<? extends Event>) clazz, new ArrayList<EventData>()
                    {
                        {
                            this.add(data);
                        }
                    });
                }
            }
        }
    }

    /**
     * Unregisters event listener class
     *
     * @param eventHandlerClass event listener class to unregister
     *
     * @see Event
     *
     * @since 1.0.0
     */
    public void unregister(Class<?> eventHandlerClass)
    {
        Object eventClassInstance = null;

        try
        {
            eventClassInstance = eventHandlerClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }

        for(ArrayList<EventData> flexibleArray : REGISTRY_MAP.values())
        {
            for(int i = flexibleArray.size() - 1; i >= 0; i--)
            {
                if(flexibleArray.get(i).source.equals(eventClassInstance))
                {
                    flexibleArray.remove(i);
                }
            }
        }

        cleanMap(true);
    }

    /**
     * Calls event
     *
     * @param event event to call
     *
     * @see Event
     *
     * @since 1.0.0
     */
    public void call(Event event)
    {
        final ArrayList<EventData> eventDataList = get(event.getClass());

        if(eventDataList != null)
        {
            for(EventData eventData : eventDataList)
            {
                try
                {
                    eventData.target.invoke(eventData.source, event);
                }
                catch (IllegalAccessException | InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}