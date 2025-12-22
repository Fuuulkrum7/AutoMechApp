package com.example.automechapp.stats;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.automechapp.MainActivity;
import com.example.automechapp.R;
import com.example.automechapp.database.GetStats;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StatsFragment extends Fragment {

    private Stats stats;

    // time
    private TextView tvTotalTime;
    private TextView tvLaunchCount;
    private TextView tvTimeBreakdowns;
    private TextView tvTimeCars;
    private TextView tvTimeOwners;
    private TextView tvLastUpdated;

    // db / derived
    private TextView tvCarsInRepair;
    private TextView tvCarsCreated;
    private TextView tvCarsActive;
    private TextView tvOwnersTotal;
    private TextView tvCarsTotal;
    private TextView tvMonthCreated;
    private TextView tvMonthRepairing;
    private TextView tvMonthDone;
    private TextView tvMonthServiced;

    public StatsFragment() {}

    public static StatsFragment newInstance(Stats stats) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putLong(Stats.K_APP_LAUNCH_COUNT, stats.getAppLaunchCount());
        args.putLong(Stats.K_TOTAL_TIME_MS, stats.getTotalTimeMs());
        args.putLong(Stats.K_SESSION_TIME_MS, stats.getSessionTimeMs());
        args.putLong(Stats.K_LAST_UPDATED_MS, stats.getLastUpdatedMs());
        args.putLong(Stats.K_BREAKDOWN_TIME_MS, stats.getBreakdownsTimeMs());
        args.putLong(Stats.K_CAR_TIME_MS, stats.getCarsTimeMs());
        args.putLong(Stats.K_OWNER_TIME_MS, stats.getOwnersTimeMs());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stats = new Stats();

        Bundle args = getArguments();
        if (args != null) {
            stats.setAppLaunchCount(args.getLong(Stats.K_APP_LAUNCH_COUNT, 0L));
            stats.setTotalTimeMs(args.getLong(Stats.K_TOTAL_TIME_MS, 0L));
            stats.setSessionTimeMs(args.getLong(Stats.K_SESSION_TIME_MS, 0L));
            stats.setLastUpdatedMs(args.getLong(Stats.K_LAST_UPDATED_MS, 0L));
            stats.setBreakdownsTimeMs(args.getLong(Stats.K_BREAKDOWN_TIME_MS, 0L));
            stats.setCarsTimeMs(args.getLong(Stats.K_CAR_TIME_MS, 0L));
            stats.setOwnersTimeMs(args.getLong(Stats.K_OWNER_TIME_MS, 0L));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats, container, false);

        // time
        tvTotalTime = v.findViewById(R.id.stats_total_time);
        tvLaunchCount = v.findViewById(R.id.stats_launch_count);
        tvTimeBreakdowns = v.findViewById(R.id.stats_time_breakdowns);
        tvTimeCars = v.findViewById(R.id.stats_time_cars);
        tvTimeOwners = v.findViewById(R.id.stats_time_owners);
        tvLastUpdated = v.findViewById(R.id.stats_last_updated);

        // db / derived (если этих view нет в layout — будут null, код не упадёт)
        tvCarsInRepair = v.findViewById(R.id.stats_cars_in_repair);
        tvCarsCreated = v.findViewById(R.id.stats_cars_created);
        tvCarsActive = v.findViewById(R.id.stats_cars_active);
        tvOwnersTotal = v.findViewById(R.id.stats_owners_total);
        tvCarsTotal = v.findViewById(R.id.stats_cars_total);
        tvMonthCreated = v.findViewById(R.id.stats_month_created);
        tvMonthRepairing = v.findViewById(R.id.stats_month_repairing);
        tvMonthDone = v.findViewById(R.id.stats_month_done);
        tvMonthServiced = v.findViewById(R.id.stats_month_serviced);

        Button btnReset = v.findViewById(R.id.stats_btn_reset);

        renderAll();

        btnReset.setOnClickListener(view -> {
            long now = System.currentTimeMillis();
            if (getContext() != null) stats.clearTimeStats(getContext(), now);
            renderAll();
        });

        // загрузка DB-статистики
        loadDbStatsAsync();

        return v;
    }

    private void loadDbStatsAsync() {
        if (getContext() == null) return;

        Thread t = new Thread(() -> {
            GetStats gs = new GetStats(getContext());
            gs.start();
            try {
                gs.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            Stats dbStats = gs.getStats();
            if (dbStats == null) return;

            // переносим только то, что из БД/расчётов, не трогая time из Bundle
            stats.setMonthStartMs(dbStats.getMonthStartMs());
            stats.setOwnersTotal(dbStats.getOwnersTotal());
            stats.setCarsTotal(dbStats.getCarsTotal());

            stats.setCarsInRepairNow(dbStats.getCarsInRepairNow());
            stats.setCarsWithCreatedBreakdownsNow(dbStats.getCarsWithCreatedBreakdownsNow());
            stats.setCarsWithActiveBreakdownsNow(dbStats.getCarsWithActiveBreakdownsNow());

            stats.setBreakdownsCreatedThisMonth(dbStats.getBreakdownsCreatedThisMonth());
            stats.setBreakdownsRepairingThisMonth(dbStats.getBreakdownsRepairingThisMonth());
            stats.setBreakdownsDoneThisMonth(dbStats.getBreakdownsDoneThisMonth());
            stats.setServicedInWorkshopThisMonth(dbStats.getServicedInWorkshopThisMonth());

            if (isAdded()) {
                requireActivity().runOnUiThread(this::renderAll);
            }
        });
        t.start();
    }

    private void renderAll() {
        renderTimeStats();
        renderDbStats();
    }

    private void renderTimeStats() {
        if (tvTotalTime != null) tvTotalTime.setText(formatDuration(stats.getTotalTimeMs()));
        if (tvLaunchCount != null) tvLaunchCount.setText(String.valueOf(stats.getAppLaunchCount()));
        if (tvTimeBreakdowns != null) tvTimeBreakdowns.setText(formatDuration(stats.getBreakdownsTimeMs()));
        if (tvTimeCars != null) tvTimeCars.setText(formatDuration(stats.getCarsTimeMs()));
        if (tvTimeOwners != null) tvTimeOwners.setText(formatDuration(stats.getOwnersTimeMs()));

        if (tvLastUpdated != null) {
            long upd = stats.getLastUpdatedMs();
            if (upd <= 0L) {
                tvLastUpdated.setText("Обновлено: —");
            } else {
                CharSequence ds = DateFormat.format("dd.MM.yyyy HH:mm", new Date(upd));
                tvLastUpdated.setText("Обновлено: " + ds);
            }
        }
    }

    private void renderDbStats() {
        if (tvCarsInRepair != null) tvCarsInRepair.setText(String.valueOf(stats.getCarsInRepairNow()));
        if (tvCarsCreated != null) tvCarsCreated.setText(String.valueOf(stats.getCarsWithCreatedBreakdownsNow()));
        if (tvCarsActive != null) tvCarsActive.setText(String.valueOf(stats.getCarsWithActiveBreakdownsNow()));

        if (tvOwnersTotal != null) tvOwnersTotal.setText(String.valueOf(stats.getOwnersTotal()));
        if (tvCarsTotal != null) tvCarsTotal.setText(String.valueOf(stats.getCarsTotal()));

        if (tvMonthCreated != null) tvMonthCreated.setText(String.valueOf(stats.getBreakdownsCreatedThisMonth()));
        if (tvMonthRepairing != null) tvMonthRepairing.setText(String.valueOf(stats.getBreakdownsRepairingThisMonth()));
        if (tvMonthDone != null) tvMonthDone.setText(String.valueOf(stats.getBreakdownsDoneThisMonth()));
        if (tvMonthServiced != null) tvMonthServiced.setText(String.valueOf(stats.getServicedInWorkshopThisMonth()));
    }

    private String formatDuration(long ms) {
        if (ms < 0) ms = 0;

        long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(ms);
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;

        if (hours <= 0) {
            return String.format(Locale.getDefault(), "%d мин", minutes);
        }
        return String.format(Locale.getDefault(), "%d ч %d мин", hours, minutes);
    }

    public void onStop() {
        super.onStop();

        if (getContext() != null) {
            stats.setLastUpdatedMs(System.currentTimeMillis());
            stats.saveTimeStats(getContext());
        }
    }
}
