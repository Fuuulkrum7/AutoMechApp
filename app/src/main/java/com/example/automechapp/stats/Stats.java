package com.example.automechapp.stats;

import android.content.Context;
import android.content.SharedPreferences;

public class Stats {

    // --- usage time (app) ---
    private long sessionTimeMs;
    private long totalTimeMs;
    private long breakdownsTimeMs;
    private long carsTimeMs;
    private long ownersTimeMs;
    private long lastUpdatedMs;

    // --- counters ---
    private long appLaunchCount;

    // --- workshop (from DB / derived) ---
    private int carsInRepairNow;               // REPAIRING
    private int carsWithCreatedBreakdownsNow;  // CREATED
    private int carsWithActiveBreakdownsNow;   // CREATED + REPAIRING
    private int ownersTotal;
    private int carsTotal;

    // --- current month period ---
    private long monthStartMs;
    private int breakdownsCreatedThisMonth;
    private int breakdownsRepairingThisMonth;
    private int breakdownsDoneThisMonth;
    private int servicedInWorkshopThisMonth;

    private boolean enabled = true;
    private boolean saved = false;

    // --- prefs ---
    private static final String PREFS = "automech_stats";

    private static final String K_SAVED = "saved";

    protected static final String K_TOTAL_TIME_MS = "totalTimeMs";
    protected static final String K_SESSION_TIME_MS = "sessionTimeMs";
    protected static final String K_BREAKDOWN_TIME_MS = "breakdownTimeMs";

    protected static final String K_CAR_TIME_MS = "carTimeMs";
    protected static final String K_OWNER_TIME_MS = "ownerTimeMs";
    protected static final String K_LAST_UPDATED_MS = "lastUpdatedMs";

    protected static final String K_APP_LAUNCH_COUNT = "appLaunchCount";

    // =========================
    // getters / setters
    // =========================

    public long getSessionTimeMs() { return sessionTimeMs; }
    public void setSessionTimeMs(long v) { this.sessionTimeMs = v; }

    public long getTotalTimeMs() { return totalTimeMs; }
    public void setTotalTimeMs(long v) { this.totalTimeMs = v; }

    public long getBreakdownsTimeMs() { return breakdownsTimeMs; }
    public void setBreakdownsTimeMs(long v) { this.breakdownsTimeMs = v; }

    public long getCarsTimeMs() { return carsTimeMs; }
    public void setCarsTimeMs(long v) { this.carsTimeMs = v; }

    public long getOwnersTimeMs() { return ownersTimeMs; }
    public void setOwnersTimeMs(long v) { this.ownersTimeMs = v; }

    public long getLastUpdatedMs() { return lastUpdatedMs; }
    public void setLastUpdatedMs(long v) { this.lastUpdatedMs = v; }

    public long getAppLaunchCount() { return appLaunchCount; }
    public void setAppLaunchCount(long v) { this.appLaunchCount = v; }

    public int getCarsInRepairNow() { return carsInRepairNow; }
    public void setCarsInRepairNow(int v) { this.carsInRepairNow = v; }

    public int getCarsWithCreatedBreakdownsNow() { return carsWithCreatedBreakdownsNow; }
    public void setCarsWithCreatedBreakdownsNow(int v) { this.carsWithCreatedBreakdownsNow = v; }

    public int getCarsWithActiveBreakdownsNow() { return carsWithActiveBreakdownsNow; }
    public void setCarsWithActiveBreakdownsNow(int v) { this.carsWithActiveBreakdownsNow = v; }

    public int getOwnersTotal() { return ownersTotal; }
    public void setOwnersTotal(int v) { this.ownersTotal = v; }

    public int getCarsTotal() { return carsTotal; }
    public void setCarsTotal(int v) { this.carsTotal = v; }

    public long getMonthStartMs() { return monthStartMs; }
    public void setMonthStartMs(long v) { this.monthStartMs = v; }

    public int getBreakdownsCreatedThisMonth() { return breakdownsCreatedThisMonth; }
    public void setBreakdownsCreatedThisMonth(int v) { this.breakdownsCreatedThisMonth = v; }

    public int getBreakdownsRepairingThisMonth() { return breakdownsRepairingThisMonth; }
    public void setBreakdownsRepairingThisMonth(int v) { this.breakdownsRepairingThisMonth = v; }

    public int getBreakdownsDoneThisMonth() { return breakdownsDoneThisMonth; }
    public void setBreakdownsDoneThisMonth(int v) { this.breakdownsDoneThisMonth = v; }

    public int getServicedInWorkshopThisMonth() { return servicedInWorkshopThisMonth; }
    public void setServicedInWorkshopThisMonth(int v) { this.servicedInWorkshopThisMonth = v; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean v) { this.enabled = v; }

    public boolean isSaved() { return saved; }
    public void setSaved(boolean v) { this.saved = v; }

    // =========================
    // persistence (app usage time)
    // =========================

    public void addLaunch() {
        appLaunchCount += 1;
    }

    public void updateTotal(long ms) {
        totalTimeMs += ms;
    }

    public void clearTimeStats(Context context, long lastUpdatedMs) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        p.edit()
                .putBoolean(K_SAVED, true)
                .putLong(K_TOTAL_TIME_MS, 0)
                .putLong(K_BREAKDOWN_TIME_MS, 0)
                .putLong(K_CAR_TIME_MS, 0)
                .putLong(K_OWNER_TIME_MS, 0)
                .putLong(K_LAST_UPDATED_MS, lastUpdatedMs)
                .putLong(K_APP_LAUNCH_COUNT, 0)
                .apply();
        this.saved = true;
        this.totalTimeMs = 0;
        this.breakdownsTimeMs = 0;
        this.carsTimeMs = 0;
        this.ownersTimeMs = 0;
        this.appLaunchCount = 0;
        this.lastUpdatedMs = lastUpdatedMs;
    }

    public void saveTimeStats(Context context) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        p.edit()
                .putBoolean(K_SAVED, true)
                .putLong(K_TOTAL_TIME_MS, totalTimeMs)
                .putLong(K_BREAKDOWN_TIME_MS, breakdownsTimeMs)
                .putLong(K_CAR_TIME_MS, carsTimeMs)
                .putLong(K_OWNER_TIME_MS, ownersTimeMs)
                .putLong(K_LAST_UPDATED_MS, lastUpdatedMs)
                .putLong(K_APP_LAUNCH_COUNT, appLaunchCount)
                .apply();
        saved = true;
    }

    public static Stats loadTimeStats(Context context) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        Stats s = new Stats();

        s.saved = p.getBoolean(K_SAVED, false);

        s.totalTimeMs = p.getLong(K_TOTAL_TIME_MS, 0L);
        s.breakdownsTimeMs = p.getLong(K_BREAKDOWN_TIME_MS, 0L);
        s.carsTimeMs = p.getLong(K_CAR_TIME_MS, 0L);
        s.ownersTimeMs = p.getLong(K_OWNER_TIME_MS, 0L);
        s.lastUpdatedMs = p.getLong(K_LAST_UPDATED_MS, 0L);

        s.appLaunchCount = p.getLong(K_APP_LAUNCH_COUNT, 0L);

        return s;
    }
}
