package com.example.automechapp.database;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;

public class DeleteBreakdowns extends Thread {
    DatabaseInterface database;

    ArrayList<Integer> breakdowns_id;
    Context context;

    public DeleteBreakdowns(DatabaseInterface database, ArrayList<Integer> breakdowns_id){
        this.database = database;
        this.breakdowns_id = breakdowns_id;
    }

    public DeleteBreakdowns(Context context, ArrayList<Integer> breakdowns_id) {
        this(new DatabaseInterface(context), breakdowns_id);
        this.context = context;
    }

    @Override
    public void run() {
        if (breakdowns_id.size() == 0)
            return;
        String[] sarr = Arrays.stream(breakdowns_id.toArray()).map(String::valueOf).toArray(String[]::new);

        database.deleteData(DatabaseInfo.BREAKDOWNS_TABLE, null, DatabaseInfo.BREAKDOWN_ID + " in (" + String.join(", ", sarr) + ")");
    }
}
