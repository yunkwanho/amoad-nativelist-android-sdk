package com.amod.sample.infeedad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amoad.AMoAdBuildConfig;
import com.amoad.AMoAdLogger;
import com.amoad.AdItem;
import com.amoad.AdList;
import com.amoad.AdResult;
import com.amoad.InfeedAd;
import com.amoad.InfeedAdLoadListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    // TODO [SDK] 管理画面から取得したsidを入力してください
    private static final String SID = "62056d310111552c000000000000000000000000000000000000000000000000";

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private RecyclerViewAdapter mAdapter;
    private ItemLoadTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new RecyclerViewAdapter();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GrayDividerItemDecoration(this));
        recyclerView.setAdapter(mAdapter);

        moreItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("moreItems");
        menu.add("clearItems");
        menu.add("refreshItems");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ("moreItems".equals(item.getTitle())) {
            moreItems();
        } else if ("clearItems".equals(item.getTitle())) {
            mAdapter.clear();
        } else if ("refreshItems".equals(item.getTitle())) {
            mAdapter.clear();
            moreItems();
        }
        return super.onOptionsItemSelected(item);
    }

    private void moreItems() {
        if (mTask == null) {
            mTask = new ItemLoadTask();
            mExecutor.execute(mTask);
        }
    }

    class ItemLoadTask implements Runnable {
        @Override
        public void run() {
            final List<MyItem> items = new ArrayList<MyItem>();

            //アイテム取得
            for (int i = 0; i < 20; i++) {
                items.add(new MyItem("http://xxxx.yyy/thumbnail.png", "Title " + i, "Desciption " + i, "Date " + i));
            }

            //広告取得
            InfeedAd.load(getApplicationContext(), SID, new InfeedAdLoadListener() {
                @Override
                public void onLoad(AdList adList, AdResult adResult) {
                    Log.d("TAG", "onLoad()" + adResult);

                    switch (adResult) {
                        case Success:
                            //TODO 空広取得成功
                            mAdapter.addItems(mergeAds(items, adList));
                        case Empty:
                            //TODO 空広告(配信されている広告がない)
                            break;
                        case Failure:
                        default:
                            //TODO 空広取得失敗
                    }
                    mTask = null;
                }
            });
        }
    }

    static List<Object> mergeAds(List<MyItem> items, AdList adList) {
        List<Object> result = new ArrayList<Object>();

        List<AdItem> ads = adList.getAdItemList();
        Iterator<AdItem> adIterator = ads.iterator();
        int beginIndex = adList.getBeginIndex();
        int interval = adList.getInterval();

        Log.d("TAG", String.format("beginIndex:%s, interval:%s, adCount:%s", beginIndex, interval, adList.getAdItemList().size()));

        int n = items.size() + ads.size();
        Iterator<MyItem> itemIterator = items.iterator();
        for (int i = 0; i < n; i++) {
            if (isAdPosition(i, beginIndex, interval) && adIterator.hasNext()) {
                result.add(adIterator.next());
            } else if (itemIterator.hasNext()) {
                result.add(itemIterator.next());
            }
        }

        return result;
    }

    static class RecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        List<Object> mItems = new ArrayList<Object>();

        public void addItems(List<Object> items) {
            mItems.addAll(items);
            notifyDataSetChanged();
        }

        public void clear() {
            mItems.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, null));
        }

        @Override
        public void onBindViewHolder(ItemViewHolder vh, int position) {
            Object item = mItems.get(position);
            if (item instanceof AdItem) {
                onBindViewHolder(vh, (AdItem) item);
            } else if (item instanceof MyItem) {
                onBindViewHolder(vh, (MyItem) item);
            }
        }

        private void onBindViewHolder(ItemViewHolder vh, final AdItem adItem) {
            //TODO 広告用画像をダウンロードしてImageViewに設定する
            String imageUrl = adItem.getImageUrl();

            // ダウンロードした画像を設定する。サンプルではリソースイメージを使いました。
            vh.mImageView.setImageResource(R.drawable.ad);
            //タイトルショットを設定
            vh.mTitleView.setText(adItem.getTitleShort());
            //タイトルゴングを設定
            vh.mDescriptionView.setText(adItem.getTitleLong());
            //サービス名を設定
            vh.mDateView.setText(adItem.getServiceName());
            //広告クリック処理を行う
            vh.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 広告クリックの遷移処理を行う
                    adItem.onClick();

                    /*
                    //TODO または 指定スキームのクリック処理をカスタムする
                    adItem.onClickWithCustomScheme("scheme", new AdClickListener() {
                        @Override
                        public void onClick(String url) {
                            //...
                        }
                    });
                    */
                }
            });
        }

        private void onBindViewHolder(ItemViewHolder vh, MyItem item) {
            vh.mImageView.setImageResource(R.drawable.item);
            vh.mTitleView.setText(item.mTitle);
            vh.mDescriptionView.setText(item.mDescription);
            vh.mDateView.setText(item.mDate);
            vh.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //...
                }
            });
        }

    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTitleView;
        TextView mDescriptionView;
        TextView mDateView;
        View mItemView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            mTitleView = (TextView) itemView.findViewById(R.id.titleView);
            mDescriptionView = (TextView) itemView.findViewById(R.id.descriptionView);
            mDateView = (TextView) itemView.findViewById(R.id.dateView);
        }
    }

    static class MyItem {
        String mImageUrl;
        String mTitle;
        String mDescription;
        String mDate;

        MyItem(String imageUrl, String title, String description, String date) {
            mImageUrl = imageUrl;
            mTitle = title;
            mDescription = description;
            mDate = date;
        }
    }

    static class GrayDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public GrayDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    static boolean isAdPosition(int position, int beginIndex, int interval) {
        if (beginIndex < 0) {
            return false;
        }
        if (interval == 0) {
            return (position == beginIndex) ? true : false;
        } else {
            if (position < beginIndex) {
                return false;
            }

            final int index = position - beginIndex;
            final boolean isAd = (index % interval == 0) ? true : false;
            if (isAd) {
                return (index / interval >= 0);
            }
            return false;
        }
    }
}
