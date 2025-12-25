package com.example.automechapp.stats;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.automechapp.R;
import com.example.automechapp.database.GetStats;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StatsFragment extends Fragment {

    private Stats stats;

    // ===== SharedPreferences keys (оценка качества) =====
    private static final String PREFS_QUALITY = "automechapp_quality";
    private static final String K_STATS_OPEN_COUNT = "stats_open_count";
    private static final String K_RATING_CURRENT = "rating_current"; // последняя сохранённая оценка
    private static final String K_RATING_PREV = "rating_prev";       // предыдущая (на шаг старее)

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

    private AlertDialog ratingDialog;
    private boolean countedThisVisibility = false;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats, container, false);

        // time
        tvTotalTime = v.findViewById(R.id.stats_total_time);
        tvLaunchCount = v.findViewById(R.id.stats_launch_count);
        tvTimeBreakdowns = v.findViewById(R.id.stats_time_breakdowns);
        tvTimeCars = v.findViewById(R.id.stats_time_cars);
        tvTimeOwners = v.findViewById(R.id.stats_time_owners);
        tvLastUpdated = v.findViewById(R.id.stats_last_updated);

        // db / derived
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
        Button btnRate = v.findViewById(R.id.stats_btn_rate);

        renderAll();

        btnReset.setOnClickListener(view -> {
            long now = System.currentTimeMillis();
            if (getContext() != null) stats.clearTimeStats(getContext(), now);
            renderAll();
        });

        btnRate.setOnClickListener(view -> showRatingDialog(false));

        // загрузка DB-статистики
        loadDbStatsAsync();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        maybeCountOpenAndPrompt();
    }

    @Override
    public void onPause() {
        super.onPause();
        countedThisVisibility = false;
    }

    private void maybeCountOpenAndPrompt() {
        if (!isAdded() || getContext() == null) return;
        if (countedThisVisibility) return;
        countedThisVisibility = true;

        SharedPreferences sp = getContext().getSharedPreferences(PREFS_QUALITY, Context.MODE_PRIVATE);
        int opens = sp.getInt(K_STATS_OPEN_COUNT, 0);
        opens += 1;
        sp.edit().putInt(K_STATS_OPEN_COUNT, opens).apply();

        // Каждое 4 открытие
        if (opens % 4 == 0) {
            showRatingDialog(true);
        }
    }

    private void showRatingDialog(boolean periodicPrompt) {
        if (!isAdded() || getContext() == null) return;
        if (ratingDialog != null && ratingDialog.isShowing()) return;

        SharedPreferences sp = getContext().getSharedPreferences(PREFS_QUALITY, Context.MODE_PRIVATE);
        int savedCurrent = sp.getInt(K_RATING_CURRENT, 0); // последняя сохранённая оценка (0 если нет)

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_app_rating, null, false);
        TextView tvTitle = dialogView.findViewById(R.id.rate_title);
        TextView tvSubtitle = dialogView.findViewById(R.id.rate_subtitle);
        RatingBar rbCurrent = dialogView.findViewById(R.id.rating_current);
        RatingBar rbPrev = dialogView.findViewById(R.id.rating_prev);

        tvTitle.setText("Оценка приложения");

        if (savedCurrent > 0) {
            tvSubtitle.setText("Как вам приложение на текущий момент?");
        } else {
            tvSubtitle.setText(periodicPrompt
                    ? "Как вам наше приложение?"
                    : "Поставьте оценку нашему приложению");
        }

        // Верхняя — новая оценка (ввод)
        rbCurrent.setNumStars(5);
        rbCurrent.setStepSize(1.0f);
        rbCurrent.setRating(0f);

        // Нижняя — предыдущая оценка (показ)
        rbPrev.setNumStars(5);
        rbPrev.setStepSize(1.0f);
        rbPrev.setIsIndicator(true);
        rbPrev.setRating(savedCurrent > 0 ? (float) savedCurrent : 0f);

        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
        b.setView(dialogView);

        b.setNegativeButton("Позже", (d, which) -> d.dismiss());

        b.setPositiveButton("Отправить", null); // перехватим, чтобы валидировать рейтинг

        ratingDialog = b.create();
        ratingDialog.setOnShowListener(dlg -> {
            Button pos = ratingDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            pos.setOnClickListener(v -> {
                int newRating = Math.round(rbCurrent.getRating());

                if (newRating < 1 || newRating > 5) {
                    tvSubtitle.setText("Выберите оценку от 1 до 5 ⭐");
                    return;
                }

                // Сдвиг: prev <- current, current <- new
                int oldCurrent = sp.getInt(K_RATING_CURRENT, 0);
                if (oldCurrent > 0) {
                    sp.edit()
                            .putInt(K_RATING_PREV, oldCurrent)
                            .putInt(K_RATING_CURRENT, newRating)
                            .apply();
                } else {
                    sp.edit()
                            .putInt(K_RATING_PREV, 0)
                            .putInt(K_RATING_CURRENT, newRating)
                            .apply();
                }

                ratingDialog.dismiss();
            });
        });

        ratingDialog.show();
    }

    private void loadDbStatsAsync() {
        if (getContext() == null) return;

        GetStats gs = new GetStats(getContext());
        gs.setRunnable(() -> {
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
        gs.start();
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

    @Override
    public void onStop() {
        super.onStop();

        if (getContext() != null) {
            stats.setLastUpdatedMs(System.currentTimeMillis());
            stats.saveTimeStats(getContext());
        }
    }
}
