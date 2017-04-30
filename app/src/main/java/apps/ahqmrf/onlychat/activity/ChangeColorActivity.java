package apps.ahqmrf.onlychat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;

import apps.ahqmrf.onlychat.R;
import apps.ahqmrf.onlychat.adapter.ChatColorAdapter;
import apps.ahqmrf.onlychat.utils.Constants;
import apps.ahqmrf.onlychat.utils.GridSpacingItemDecoration;

public class ChangeColorActivity extends AppCompatActivity implements ChatColorAdapter.Callback{

    private RecyclerView mColorListView;
    private ArrayList<Integer> mColorItems;
    private ChatColorAdapter mColorAdapter;

    private int colorResIds[] = {
            R.color.orange, R.color.colorPrimary, R.color.amber, R.color.black,
            R.color.teal, R.color.blue, R.color.blue_grey, R.color.light_blue,
            R.color.light_green, R.color.brown, R.color.yellow, R.color.red,
            R.color.purple, R.color.deep_orange, R.color.deep_purple, R.color.pink,
            R.color.cyan, R.color.green, R.color.grey, R.color.lime, R.color.indigo
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_color);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mColorListView = (RecyclerView) findViewById(R.id.rv_color);
        mColorItems = new ArrayList<>();

        for (int i = 0; i < colorResIds.length; i++) {
            mColorItems.add(colorResIds[i]);
        }

        mColorAdapter = new ChatColorAdapter(this, mColorItems, this);
        mColorListView.setLayoutManager(new GridLayoutManager(this, 4));
        mColorListView.setAdapter(mColorAdapter);
        int spanCount = 4; // 3 columns
        int spacing = 3; // 3px
        boolean includeEdge = false;
        mColorListView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onColorClick(int color) {
        Intent intent = new Intent();
        intent.putExtra(Constants.Utils.SELECTED_COLOR, color);
        setResult(RESULT_OK, intent);
        finish();
    }
}
