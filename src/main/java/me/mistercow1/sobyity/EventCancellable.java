package me.mistercow1.sobyity;

public class EventCancellable extends Event
{
    private boolean cancelled = false;

    /**
     * Returns a boolean that specifies if the event is cancelled
     *
     * @return a boolean that specifies if the event is cancelled
     *
     * @since 1.0.0
     */
    public boolean getCancelled()
    {
        return cancelled;
    }

    /**
     * Sets an event to be cancelled
     *
     * @param cancelled a boolean to specify wherever or not to cancel the event
     *
     * @since 1.0.0
     */
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }
}