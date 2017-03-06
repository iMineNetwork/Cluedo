package nl.imine.minigame.timer;

import org.bukkit.entity.Player;

public interface Timer {

    /**
     * Set The title of the timer to the given string.
     *
     * @param title
     */
    void setTitle(String title);

    /**
     * Returns the current time left on this timer.
     *
     * @return The current time left on the timer.
     */
    int getTimer();

    /**
     * Decrements the timers value by one unit.
     */
    void decrement();

    /**
     * Resets the Timer to a new max value.
     *
     * @param startTime
     *            The new max value from which the timer will tick.
     */
    void resetTimer(int startTime);

    /**
     * Show the Timer to the given player.
     *
     * @param player
     *            The player to show the timer.
     */
    void showTimer(Player player);

    /**
     * Hides the timer for a player.
     *
     * @param player
     *            The player to hide the timer from.
     */
    void hideTimer(Player player);
}
