package com.mindgarden.app.garden;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public final class UnlockRepository {
    private static final String PREFS = "garden_unlocks";
    private static final String INITIALIZED = "initialized";

    private UnlockRepository() { }

    public static void initialize(Context context, int totalMemos) {
        SharedPreferences preferences = preferences(context);
        if (preferences.getBoolean(INITIALIZED, false)) {
            return;
        }
        SharedPreferences.Editor editor = preferences.edit().putBoolean(INITIALIZED, true);
        for (GardenRules.Structure structure : GardenRules.STRUCTURES) {
            if (totalMemos >= structure.threshold) {
                editor.putBoolean(key(structure), true);
            }
        }
        editor.apply();
    }

    public static List<GardenRules.Structure> refresh(Context context, int totalMemos) {
        SharedPreferences preferences = preferences(context);
        List<GardenRules.Structure> newlyUnlocked = new ArrayList<>();
        SharedPreferences.Editor editor = preferences.edit();
        for (GardenRules.Structure structure : GardenRules.STRUCTURES) {
            if (totalMemos >= structure.threshold
                    && !preferences.getBoolean(key(structure), false)) {
                editor.putBoolean(key(structure), true);
                newlyUnlocked.add(structure);
            }
        }
        editor.apply();
        return newlyUnlocked;
    }

    public static boolean isUnlocked(Context context, GardenRules.Structure structure) {
        return preferences(context).getBoolean(key(structure), false);
    }

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static String key(GardenRules.Structure structure) {
        return "unlocked_" + structure.id;
    }
}
