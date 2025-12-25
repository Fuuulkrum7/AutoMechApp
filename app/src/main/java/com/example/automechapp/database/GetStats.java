package com.example.automechapp.database;

import static com.example.automechapp.database.DatabaseInfo.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import com.example.automechapp.breakdown.BreakdownStates;
import com.example.automechapp.stats.Stats;

import java.util.Calendar;

public class GetStats extends Thread {
    private final Context context;
    private Stats stats;
    private Runnable onDone;

    public GetStats(Context context) {
        this.context = context;
    }

    public void setRunnable(Runnable onDone) {
        this.onDone = onDone;
    }

    void onFinish() {
        if (onDone == null) {
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(onDone);
    }

    @Override
    public void run() {
        try {
            DatabaseInterface databaseInterface = new DatabaseInterface(context);
            SQLiteDatabase db = databaseInterface.getReadableDatabase();

            Stats s = Stats.loadTimeStats(context);

            long monthStartMs = getMonthStartMs();
            s.setMonthStartMs(monthStartMs);

            // totals
            s.setOwnersTotal((int) countAll(db, OWNERS_TABLE));
            s.setCarsTotal((int) countAll(db, CARS_TABLE));

            // now (distinct cars by breakdown state)
            s.setCarsInRepairNow((int) countDistinct(db, BREAKDOWNS_TABLE, CAR_ID,
                    BREAKDOWN_STATE + "=?",
                    new String[]{String.valueOf(BreakdownStates.REPAIRING.ordinal())}));

            s.setCarsWithCreatedBreakdownsNow((int) countDistinct(db, BREAKDOWNS_TABLE, CAR_ID,
                    BREAKDOWN_STATE + "=?",
                    new String[]{String.valueOf(BreakdownStates.CREATED.ordinal())}));

            s.setCarsWithActiveBreakdownsNow((int) countDistinct(db, BREAKDOWNS_TABLE, CAR_ID,
                    BREAKDOWN_STATE + " IN (?,?)",
                    new String[]{
                            String.valueOf(BreakdownStates.CREATED.ordinal()),
                            String.valueOf(BreakdownStates.REPAIRING.ordinal())
                    }));

            // month: created by STANDARD_DATE
            s.setBreakdownsCreatedThisMonth((int) countWhere(db, BREAKDOWNS_TABLE,
                    STANDARD_DATE + ">=?",
                    new String[]{String.valueOf(monthStartMs)}));

            // month: repairing (created this month + state=REPAIRING)
            s.setBreakdownsRepairingThisMonth((int) countWhere(db, BREAKDOWNS_TABLE,
                    STANDARD_DATE + ">=? AND " + BREAKDOWN_STATE + "=?",
                    new String[]{String.valueOf(monthStartMs), String.valueOf(BreakdownStates.REPAIRING.ordinal())}));

            // month: done by EDIT_TIME (completion time)
            s.setBreakdownsDoneThisMonth((int) countWhere(db, BREAKDOWNS_TABLE,
                    EDIT_TIME + ">=? AND " + BREAKDOWN_STATE + "=?",
                    new String[]{String.valueOf(monthStartMs), String.valueOf(BreakdownStates.DONE.ordinal())}));

            // alias
            s.setServicedInWorkshopThisMonth(s.getBreakdownsDoneThisMonth());

            stats = s;
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Stats getStats() {
        return stats;
    }

    private long getMonthStartMs() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private long countAll(SQLiteDatabase db, String table) {
        return countWhere(db, table, null, null);
    }

    private long countWhere(SQLiteDatabase db, String table, String where, String[] args) {
        String sql = "SELECT COUNT(*) FROM " + table + (where == null ? "" : " WHERE " + where);
        Cursor c = null;
        try {
            c = db.rawQuery(sql, args);
            if (c.moveToFirst()) return c.getLong(0);
            return 0L;
        } finally {
            if (c != null) c.close();
        }
    }

    private long countDistinct(SQLiteDatabase db, String table, String distinctColumn, String where, String[] args) {
        String sql = "SELECT COUNT(DISTINCT " + distinctColumn + ") FROM " + table +
                (where == null ? "" : " WHERE " + where);
        Cursor c = null;
        try {
            c = db.rawQuery(sql, args);
            if (c.moveToFirst()) return c.getLong(0);
            return 0L;
        } finally {
            if (c != null) c.close();
        }
    }
}
