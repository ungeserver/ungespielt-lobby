package net.ungespielt.lobby.spigot.rx.scheduler;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

/**
 * The scheduler implementation that adapts bukkit schedulers into the rx java structure.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class SpigotRxScheduler extends Scheduler {

    /**
     * If this scheduler is synchronous.
     */
    private final boolean sync;

    /**
     * The bukkit scheduler.
     */
    private final BukkitScheduler scheduler;

    /**
     * The bukkit plugin.
     */
    private final Plugin plugin;

    /**
     * Create a new scheduler.
     *  @param sync If the scheduler is synchronous.
     * @param scheduler The bukkit scheduler.
     * @param plugin The bukkit plugin.
     */
    public SpigotRxScheduler(boolean sync, BukkitScheduler scheduler, Plugin plugin) {
        this.sync = sync;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public Worker createWorker() {
        return new SpigotRxWorker();
    }

    /**
     * Schedule a delayed task with the given runnable and the delay. Based on the {@link #sync} we will execute
     * {@link #scheduleSync(Runnable, int)} or {@link #scheduleAsync(Runnable, int)}.
     *
     * @param runnable   The runnable.
     * @param ticksDelay The delay.
     * @return The bukkit task instance.
     */
    private BukkitTask schedule(Runnable runnable, int ticksDelay) {
        return sync ? scheduleSync(runnable, ticksDelay) : scheduleAsync(runnable, ticksDelay);
    }

    /**
     * Schedule an async delay bukkit task with the given runnable and delay.
     *
     * @param runnable   The runnable.
     * @param ticksDelay The delay.
     * @return The bukkit task instance.
     */
    private BukkitTask scheduleAsync(Runnable runnable, int ticksDelay) {
        return scheduler.runTaskLaterAsynchronously(plugin, runnable, ticksDelay);
    }

    /**
     * Schedule a sync delay bukkit task with the given runnable and delay.
     *
     * @param runnable   The runnable.
     * @param ticksDelay The delay.
     * @return The bukkit task instance.
     */
    private BukkitTask scheduleSync(Runnable runnable, int ticksDelay) {
        return scheduler.runTaskLater(plugin, runnable, ticksDelay);
    }

    /**
     * Schedule a repeating task with the given runnable after the given delay with the given period. Based on the
     * {@link #sync} we will execute {@link #scheduleSync(Runnable, int, int)} or
     * {@link #scheduleAsync(Runnable, int, int)}.
     *
     * @param runnable     The runnable.
     * @param initialDelay The initial delay.
     * @param period       The period.
     * @return The bukkit task instance.
     */
    private BukkitTask schedule(Runnable runnable, int initialDelay, int period) {
        return sync ? scheduleSync(runnable, initialDelay, period) : scheduleAsync(runnable, initialDelay, period);
    }

    /**
     * Schedule an async repeating task with the given runnable after the given delay with the given period.
     *
     * @param runnable     The runnable.
     * @param initialDelay The initial delay.
     * @param period       The period.
     * @return The bukkit task instance.
     */
    private BukkitTask scheduleAsync(Runnable runnable, int initialDelay, int period) {
        return scheduler.runTaskTimerAsynchronously(plugin, runnable, initialDelay, period);
    }

    /**
     * Schedule a sync repeating task with the given runnable after the given delay with the given period.
     *
     * @param runnable     The runnable.
     * @param initialDelay The initial delay.
     * @param period       The period.
     * @return The bukkit task instance.
     */
    private BukkitTask scheduleSync(Runnable runnable, int initialDelay, int period) {
        return scheduler.runTaskTimer(plugin, runnable, initialDelay, period);
    }

    /**
     * Schedule an task immediately. Based on the {@link #sync} we will execute {@link #scheduleSync(Runnable)} or
     * {@link #scheduleAsync(Runnable)}.
     *
     * @param runnable The runnable.
     * @return The bukkit task instance.
     */
    private BukkitTask schedule(Runnable runnable) {
        return sync ? scheduleSync(runnable) : scheduleAsync(runnable);
    }

    /**
     * Execute the given runnable in an async bukkit scheduler.
     *
     * @param runnable The runnable.
     * @return The bukkit task instance.
     */
    private BukkitTask scheduleAsync(Runnable runnable) {
        return scheduler.runTaskAsynchronously(plugin, runnable);
    }

    /**
     * Execute the given runnable in a sync bukkit scheduler.
     *
     * @param runnable The runnable.
     * @return The bukkit task instance.
     */
    private BukkitTask scheduleSync(Runnable runnable) {
        return scheduler.runTask(plugin, runnable);
    }

    /**
     * The worker implementation.
     */
    private final class SpigotRxWorker extends Worker {

        /**
         * The compostie disposable for all workers.
         */
        private final CompositeDisposable compositeDisposable = new CompositeDisposable();

        @Override
        public Disposable schedule(Runnable runnable, long delay, TimeUnit unit) {
            BukkitTask bukkitTask = SpigotRxScheduler.this.schedule(runnable, convertTimeToTicks(delay, unit));
            Disposable disposable = new DisposableBukkitTask(bukkitTask);
            compositeDisposable.add(disposable);
            return disposable;
        }

        @Override
        public Disposable schedulePeriodically(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
            BukkitTask bukkitTask = SpigotRxScheduler.this.schedule(runnable, convertTimeToTicks(initialDelay, unit), convertTimeToTicks(period, unit));
            Disposable disposable = new DisposableBukkitTask(bukkitTask);
            compositeDisposable.add(disposable);
            return disposable;
        }

        @Override
        public Disposable schedule(Runnable runnable) {
            BukkitTask bukkitTask = SpigotRxScheduler.this.schedule(runnable);
            Disposable disposable = new DisposableBukkitTask(bukkitTask);
            compositeDisposable.add(disposable);
            return disposable;
        }

        /**
         * Convert java time to bukkit ticks.
         *
         * @param time     The time.
         * @param timeUnit The time unit.
         * @return The time in bukkit ticks.
         */
        private int convertTimeToTicks(long time, TimeUnit timeUnit) {
            return (int) Math.round((double) timeUnit.toMillis(time) / 50D);
        }

        @Override
        public void dispose() {
            compositeDisposable.dispose();
        }

        @Override
        public boolean isDisposed() {
            return compositeDisposable.isDisposed();
        }

        /**
         * Wrap a bukkit task in a disposable.
         */
        private final class DisposableBukkitTask implements Disposable {

            /**
             * The bukkit task we want to dispose.
             */
            private final BukkitTask bukkitTask;

            /**
             * If the task is disposed.
             */
            private boolean disposed;

            /**
             * Create a new disposable bukkit task.
             *
             * @param bukkitTask The bukkit task.
             */
            DisposableBukkitTask(BukkitTask bukkitTask) {
                this.bukkitTask = bukkitTask;
            }

            @Override
            public void dispose() {
                disposed = true;
                this.bukkitTask.cancel();
            }

            @Override
            public boolean isDisposed() {
                return disposed && !bukkitTask.getOwner().getServer().getScheduler().isCurrentlyRunning(bukkitTask.getTaskId());
            }
        }
    }
}
