package com.mindgarden.app;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mindgarden.app.data.MemoDAO;
import com.mindgarden.app.data.MemoDTO;
import com.mindgarden.app.databinding.ActivityMemoListBinding;
import com.mindgarden.app.garden.GardenRules;
import com.mindgarden.app.garden.UnlockRepository;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MemoListActivity extends Activity {
    private ActivityMemoListBinding binding;
    private MemoDAO dao;
    private final List<MemoDTO> memos = new ArrayList<>();
    private MemoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMemoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dao = new MemoDAO(this);

        adapter = new MemoAdapter();
        binding.lvMemos.setAdapter(adapter);
        binding.lvMemos.setEmptyView(binding.tvEmpty);
        binding.lvMemos.setOnItemClickListener((parent, view, position, id) -> {
            MemoDTO selected = (MemoDTO) parent.getItemAtPosition(position);
            Intent intent = new Intent(this, MemoEditorActivity.class);
            intent.putExtra(MemoEditorActivity.EXTRA_MEMO_ID, selected.getId());
            startActivity(intent);
        });
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnPlantSeed.setOnClickListener(v ->
                startActivity(new Intent(this, MemoEditorActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            refresh();
            List<GardenRules.Structure> newlyUnlocked =
                    UnlockRepository.refresh(this, dao.countAll());
            if (!newlyUnlocked.isEmpty()) {
                Toast.makeText(this,
                        getString(R.string.structure_unlocked,
                                newlyUnlocked.get(0).name, newlyUnlocked.get(0).message),
                        Toast.LENGTH_LONG).show();
            }
        } catch (SQLiteException e) {
            Toast.makeText(this, R.string.database_error, Toast.LENGTH_LONG).show();
        }
    }

    private void refresh() {
        memos.clear();
        memos.addAll(dao.selectAll());
        adapter.notifyDataSetChanged();
        binding.tvListCount.setText(getString(R.string.memo_list_count, memos.size()));
    }

    @Override
    protected void onDestroy() {
        if (dao != null) {
            dao.close();
        }
        super.onDestroy();
    }

    private final class MemoAdapter extends ArrayAdapter<MemoDTO> {
        MemoAdapter() {
            super(MemoListActivity.this, R.layout.item_memo, memos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                row = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_memo, parent, false);
            }
            MemoDTO memo = getItem(position);
            TextView title = row.findViewById(R.id.tvMemoPreview);
            TextView detail = row.findViewById(R.id.tvMemoMeta);
            if (memo != null) {
                title.setText("✿  " + memo.getContent());
                String date = DateFormat.getDateInstance(DateFormat.MEDIUM)
                        .format(new Date(memo.getCreatedAt()));
                detail.setText(getString(R.string.memo_metadata, memo.getKeyword(),
                        memo.getSource(), date));
            }
            return row;
        }
    }
}
