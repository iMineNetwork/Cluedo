package nl.imine.minigame.timer;

import java.util.ArrayList;

public class TimerRunner implements Runnable {

	private ArrayList<Timer> timers = new ArrayList<>();

	@Override
	public void run() {
		//Create a copy of the timer list to prevent concurrency issues.
		//Due to working in a thread, Iterator wasn't working.
		for (Timer timer : new ArrayList<>(timers)) {
			if (!timer.isStopped()) {
				timer.decrement();
				//No need in ticking timers when they are done.
				if (timer.getTimer() <= 0) {
					removeTimer(timer);
				}
			}
		}
	}

	public void addTimer(Timer timer) {
		timers.add(timer);
	}

	public void removeTimer(Timer timer) {
		timers.remove(timer);
	}
}
