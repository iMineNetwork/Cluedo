package nl.imine.minigame.timer;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class TimerImpl implements Timer{

    private List<TimerHandler> handlers;

    private final BossBar bossbar;
    private int maxTime;
    private int timer;
    private boolean stopped = false;

    public TimerImpl(String title, int maxTime, TimerHandler... handlers){
        this.maxTime = maxTime;
        this.timer = maxTime;
        bossbar = Bukkit.createBossBar(title, BarColor.GREEN, BarStyle.SEGMENTED_20);
        bossbar.setProgress(1);
        this.handlers = Arrays.asList(handlers);
    }

    @Override
    public void setTitle(String title) {
        bossbar.setTitle(title);
    }

    @Override
    public void resetTimer(int startTime) {
        this.maxTime = startTime;
        setTimer(startTime);
    }

    @Override
    public int getTimer() {
        return timer;
    }

    @Override
    public void showTimer(Player player) {
        bossbar.addPlayer(player);
    }

    @Override
    public void hideTimer(Player player) {
        bossbar.removePlayer(player);
    }

    @Override
    public void decrement() {
        setTimer(getTimer() - 1);
    }

    private void setTimer(int timer) {
        if (timer >= maxTime) {
            this.timer = maxTime;
        } else if (timer <= 0) {
            this.timer = 0;
            handlers.forEach(TimerHandler::onTimerEnd);
        } else {
            this.timer = timer;
        }
        this.bossbar.setProgress((double) timer / (double) maxTime);
    }

    @Override
    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    @Override
    public boolean isStopped() {
        return this.stopped;
    }
}
