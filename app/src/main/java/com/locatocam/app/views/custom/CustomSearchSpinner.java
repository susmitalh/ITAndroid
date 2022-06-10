package com.locatocam.app.views.custom;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.locatocam.app.R;
import com.locatocam.app.utils.ItemData;

import java.util.ArrayList;
import java.util.List;

public class CustomSearchSpinner extends AppCompatActivity {
    RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    EditText search;
    List<ItemData> input = new ArrayList<ItemData>();
    public int req_code,row_number=-1;
    TextView close_btn,desc_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_search_spinner);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        recyclerView=(RecyclerView)findViewById(R.id.rec1) ;
        search=(EditText) findViewById(R.id.search_text);
        close_btn=(TextView) findViewById(R.id.close_btn);
        desc_text=(TextView) findViewById(R.id.label1);

        search.requestFocus();

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

       if(getIntent().getExtras().get("data_list")!=null)
       {
        input= (ArrayList<ItemData>) getIntent().getExtras().get("data_list");
           mAdapter = new ListDataAdapter(input);
           recyclerView.setAdapter(mAdapter);
       }

        if(getIntent().getExtras().get("row_number")!=null) {
        row_number= Integer.parseInt(getIntent().getExtras().get("row_number").toString());
        }

      req_code= Integer.parseInt(getIntent().getExtras().get("req_code").toString());

        if(getIntent().getExtras().get("desc_text")!=null) {
            desc_text.setText("Select "+getIntent().getExtras().get("desc_text").toString());
            if(getIntent().getExtras().get("desc_text").toString().equalsIgnoreCase("Documents Type")){
                search.setVisibility(View.GONE);
            }
        }

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                List<ItemData> input2 = new ArrayList<>();

                for (ItemData itemData:input)
                {
                    if(itemData.getName().toLowerCase().contains(editable.toString().trim().toLowerCase()))
                    {
                        input2.add(itemData);
                    }
                }
                mAdapter = new ListDataAdapter(input2);
                recyclerView.setAdapter(mAdapter);
            }
        });
    }
}
