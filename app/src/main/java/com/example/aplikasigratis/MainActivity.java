package com.example.aplikasigratis;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "aplikasi_gratis_prefs";
    private static final String KEY_DATA = "data_list";

    private EditText etInput;
    private Button btnSave;
    private ListView listView;
    private ArrayList<String> dataList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etInput = findViewById(R.id.etInput);
        btnSave = findViewById(R.id.btnSave);
        listView = findViewById(R.id.listView);

        // Muat data yang tersimpan sebelumnya
        dataList = loadData();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        setupDeleteOnLongPress();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputData = etInput.getText().toString().trim();
                if (!inputData.isEmpty()) {
                    dataList.add(inputData);
                    adapter.notifyDataSetChanged();
                    saveData();
                    etInput.setText("");
                    Toast.makeText(MainActivity.this, "Berhasil disimpan!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Kolom tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Tekan lama pada item untuk menghapusnya (dengan konfirmasi)
    private void setupDeleteOnLongPress() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Hapus catatan?")
                        .setMessage(dataList.get(position))
                        .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dataList.remove(position);
                                adapter.notifyDataSetChanged();
                                saveData();
                                Toast.makeText(MainActivity.this, "Catatan dihapus", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Batal", null)
                        .show();
                return true;
            }
        });
    }

    // Simpan seluruh daftar ke SharedPreferences sebagai teks JSON
    private void saveData() {
        JSONArray array = new JSONArray();
        for (String item : dataList) {
            array.put(item);
        }
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_DATA, array.toString()).apply();
    }

    // Baca daftar dari SharedPreferences; kalau kosong atau rusak, mulai dari daftar kosong
    private ArrayList<String> loadData() {
        ArrayList<String> result = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_DATA, null);
        if (json != null) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    result.add(array.getString(i));
                }
            } catch (JSONException e) {
                result.clear();
            }
        }
        return result;
    }
}
