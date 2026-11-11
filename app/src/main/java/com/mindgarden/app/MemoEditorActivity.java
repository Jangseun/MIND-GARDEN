package com.mindgarden.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.mindgarden.app.data.MemoDAO;
import com.mindgarden.app.data.MemoDTO;
import com.mindgarden.app.databinding.ActivityMemoEditorBinding;

public class MemoEditorActivity extends Activity {
    public static final String EXTRA_MEMO_ID = "memo_id";

    private ActivityMemoEditorBinding binding;
    private MemoDAO dao;
    private MemoDTO memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMemoEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dao = new MemoDAO(this);

        setupSpinner(binding.spSource, R.array.memo_sources);
        setupSpinner(binding.spKeyword, R.array.memo_keywords);

        long memoId = getIntent().getLongExtra(EXTRA_MEMO_ID, -1);
        if (memoId > 0) {
            loadMemo(memoId);
        } else {
            binding.btnDelete.setEnabled(false);
            binding.btnSave.setText(R.string.plant_seed);
        }

        binding.btnSave.setOnClickListener(v -> saveMemo());
        binding.btnDelete.setOnClickListener(v -> confirmDelete());
        binding.btnClear.setOnClickListener(v -> clearForm());
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupSpinner(android.widget.Spinner spinner, int arrayResource) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, arrayResource, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void loadMemo(long memoId) {
        try {
            memo = dao.selectById(memoId);
            if (memo == null) {
                Toast.makeText(this, R.string.memo_not_found, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            binding.tvEditorTitle.setText(R.string.edit_memo_title);
            binding.btnSave.setText(R.string.save_changes);
            binding.btnDelete.setEnabled(true);
            binding.etContent.setText(memo.getContent());
            selectSpinnerItem(binding.spSource, memo.getSource());
            selectSpinnerItem(binding.spKeyword, memo.getKeyword());
        } catch (SQLiteException e) {
            Toast.makeText(this, R.string.database_error, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void selectSpinnerItem(android.widget.Spinner spinner, String value) {
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) spinner.getAdapter();
        for (int position = 1; position < adapter.getCount(); position++) {
            if (value.equals(adapter.getItem(position).toString())) {
                spinner.setSelection(position);
                return;
            }
        }
    }

    private void saveMemo() {
        String content = binding.etContent.getText().toString().trim();
        int sourcePosition = binding.spSource.getSelectedItemPosition();
        int keywordPosition = binding.spKeyword.getSelectedItemPosition();
        if (sourcePosition == 0) {
            binding.spSource.setPrompt(getString(R.string.choose_source));
            Toast.makeText(this, R.string.choose_source, Toast.LENGTH_SHORT).show();
            return;
        }
        if (keywordPosition == 0) {
            Toast.makeText(this, R.string.choose_keyword, Toast.LENGTH_SHORT).show();
            return;
        }
        if (content.isEmpty()) {
            binding.etContent.setError(getString(R.string.content_required));
            binding.etContent.requestFocus();
            return;
        }

        long now = System.currentTimeMillis();
        try {
            if (memo == null) {
                MemoDTO newMemo = new MemoDTO(
                        binding.spSource.getSelectedItem().toString(),
                        binding.spKeyword.getSelectedItem().toString(),
                        content, now);
                if (dao.insert(newMemo) < 0) {
                    Toast.makeText(this, R.string.save_failed, Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(this, R.string.seed_planted, Toast.LENGTH_SHORT).show();
            } else {
                memo.setSource(binding.spSource.getSelectedItem().toString());
                memo.setKeyword(binding.spKeyword.getSelectedItem().toString());
                memo.setContent(content);
                memo.setUpdatedAt(now);
                if (dao.update(memo) <= 0) {
                    Toast.makeText(this, R.string.update_missing, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, R.string.memo_updated, Toast.LENGTH_SHORT).show();
            }
            finish();
        } catch (SQLiteException e) {
            Toast.makeText(this, R.string.database_error, Toast.LENGTH_LONG).show();
        }
    }

    private void confirmDelete() {
        if (memo == null) {
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage(R.string.confirm_delete)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteMemo())
                .show();
    }

    private void deleteMemo() {
        try {
            if (dao.delete(memo.getId()) > 0) {
                Toast.makeText(this, R.string.memo_deleted, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.delete_missing, Toast.LENGTH_SHORT).show();
            }
        } catch (SQLiteException e) {
            Toast.makeText(this, R.string.database_error, Toast.LENGTH_LONG).show();
        }
    }

    private void clearForm() {
        if (memo != null) {
            return;
        }
        binding.etContent.setText("");
        binding.spSource.setSelection(0);
        binding.spKeyword.setSelection(0);
    }

    @Override
    protected void onDestroy() {
        if (dao != null) {
            dao.close();
        }
        super.onDestroy();
    }
}
