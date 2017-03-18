package nl.imine.minigame.timer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class TimerManager {

    private TimerRunner timerRunner = new TimerRunner();

    public TimerManager(){
    }

    /**
     * Initializes the Timer system. Takes a bukkit plugin to schedule the tasks.
     * @param plugin the plugin to register the scheduled task for
     */
    public void init(Plugin plugin){
        Bukkit.getScheduler().runTaskTimer(plugin, timerRunner, 0, 20l);
    }

    /**
     * Create a new timer which instantly starts ticking every second.
     * The title is for a Bossbar to display.
     *
     * It is possible to add a handler.
     * After the timer has elapsed, the handlers {@link TimerHandler#onTimerEnd() onTimerEnd} method will be called.
     * @param title The bossbar title for the timer.
     * @param maxSeconds The initial time to countdown from.
     * @param handlers The handlers to be notified when the timer is finished.
     * @return A timer, which gets updated every second.
     */
    public Timer createTimer(String title, int maxSeconds, TimerHandler... handlers){
        Timer timer = new TimerImpl(title, maxSeconds, handlers);
        timerRunner.addTimer(timer);
        return timer;
    }

    /**
     * Destroys a timer object. This will prevent is from ever running again.
     * @param timer The timer to destroy
     */
    public void removeTimer(Timer timer){
        timerRunner.removeTimer(timer);
    }

}
