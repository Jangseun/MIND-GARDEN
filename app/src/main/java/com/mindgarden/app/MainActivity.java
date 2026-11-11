package com.mindgarden.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.mindgarden.app.data.MemoDAO;
import com.mindgarden.app.databinding.ActivityGardenBinding;
import com.mindgarden.app.garden.GardenRules;
import com.mindgarden.app.garden.GardenSceneView;
import com.mindgarden.app.garden.UnlockRepository;

import java.util.Calendar;

public class MainActivity extends Activity {
    private ActivityGardenBinding binding;
    private MemoDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGardenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dao = new MemoDAO(this);
        UnlockRepository.initialize(this, dao.countAll());

        binding.btnPlantSeed.setOnClickListener(v ->
                startActivity(new Intent(this, MemoEditorActivity.class)));
        binding.btnViewMemos.setOnClickListener(v ->
                startActivity(new Intent(this, MemoListActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dao == null) {
            return;
        }
        try {
            int total = dao.countAll();
            binding.tvSeedCount.setText(getString(R.string.seed_count, total));
            binding.tvTodayQuest.setText(getString(
                    dao.countCreatedBetween(startOfToday(), startOfTomorrow()) > 0
                            ? R.string.quest_complete : R.string.quest_incomplete));
            binding.gardenScene.setGardenState(
                    dao.countByKeyword(GardenRules.KEYWORD_PASSION),
                    dao.countByKeyword(GardenRules.KEYWORD_CARE),
                    dao.countByKeyword(GardenRules.KEYWORD_CONSISTENCY),
                    dao.countByKeyword(GardenRules.KEYWORD_RESPONSIBILITY),
                    UnlockRepository.refresh(this, total));
        } catch (RuntimeException e) {
            Toast.makeText(this, R.string.database_error, Toast.LENGTH_LONG).show();
        }
    }

    private long startOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long startOfTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTimeInMillis();
    }

    @Override
    protected void onDestroy() {
        if (dao != null) {
            dao.close();
        }
        super.onDestroy();
    }
}
