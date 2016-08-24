package com.amod.sample.infeedad;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
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

import com.amoad.AdItem;
import com.amoad.AdList;
import com.amoad.AdResult;
import com.amoad.InfeedAd;
import com.amoad.InfeedAdLoadListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    // TODO 1.管理画面から取得したsidを入力してください
    private static final String SID1 = "62056d310111552c000000000000000000000000000000000000000000000000";
    private static final String SID2 = "62056d310111552c000000000000000000000000000000000000000000000000";

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private ItemViewAdapter mAdapter;
    private ItemLoadTask mTask;
    private Parcelable mBannerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //本番以外の環境でテストを行うためのサンプルコード
        //InfeedAd.setAdRequestUrl("http://xxx");

        //ネットワーク通信の制限時間を設定する
        InfeedAd.setNetworkTimeoutMillis(5000);//５秒

        initListView();

        if (savedInstanceState == null) {
            loadBanner();
            loadItems();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO 6.ビューの監視を解除する
        InfeedAd.clearVisiblityTracking();
        super.onDestroy();
    }

    private void initListView() {
        mAdapter = new ItemViewAdapter(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setAdapter(mAdapter);
    }

    private void loadBanner() {
        InfeedAd.load(getApplicationContext(), SID2, new InfeedAdLoadListener() {
            @Override
            public void onLoad(AdList adList, AdResult adResult) {
                switch (adResult) {
                    case Success:
                        Log.d("TAG", "広告取得成功");
                        if (!adList.getAdItemList().isEmpty()) {
                            bindAdItem(adList.getAdItemList().get(0));
                        }
                        break;
                    case Empty:
                        Log.d("TAG", "空広告");
                        break;
                    case Failure:
                    default:
                        Log.d("TAG", "広告取得失敗");
                }
                mTask = null;
            }
        });
    }

    private void bindAdItem(final AdItem adItem) {
        mBannerItem = adItem;

        //画像を設定
        final ImageView banner = (ImageView) findViewById(R.id.banner);
        Picasso.with(this)
                .load(adItem.getImageUrl())
                .into(banner, new Callback() {
                    @Override
                    public void onSuccess() {
                        //広告ビューを監視する
                        InfeedAd.setVisiblityTracking(getApplicationContext(), banner, adItem);

                        ImageView banner = (ImageView) findViewById(R.id.banner);
                        banner.setVisibility(View.VISIBLE);
                        banner.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //クリック処理を行う
                                adItem.onClick(MainActivity.this);
                            }
                        });
                    }

                    @Override
                    public void onError() {
                    }
                });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("item", mBannerItem);
        outState.putParcelableArrayList("items", new ArrayList<Parcelable>(mAdapter.getItems()));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        bindAdItem((AdItem) savedInstanceState.getParcelable("item"));
        mAdapter.addItems(savedInstanceState.getParcelableArrayList("items"));
    }

    void loadItems() {
        if (mTask == null) {
            mTask = new ItemLoadTask();
            mExecutor.execute(mTask);
        }
    }

    void clearItems() {
        mAdapter.clear();
    }

    void refreshItems() {
        clearItems();
        loadItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("AddItems");
        menu.add("ClearItems");
        menu.add("RefreshItems");
        menu.add("RefreshBanner");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ("AddItems".equals(item.getTitle())) {
            loadItems();
        } else if ("ClearItems".equals(item.getTitle())) {
            clearItems();
        } else if ("RefreshItems".equals(item.getTitle())) {
            refreshItems();
        } else if ("RefreshBanner".equals(item.getTitle())) {
            loadBanner();
        }
        return super.onOptionsItemSelected(item);
    }

    class ItemLoadTask implements Runnable {

        @Override
        public void run() {

            //アイテム取得
            final List<MyItem> items = getMyItems();

            //TODO 2.広告を取得する
            InfeedAd.load(getApplicationContext(), SID1, new InfeedAdLoadListener() {
                @Override
                public void onLoad(AdList adList, AdResult adResult) {
                    switch (adResult) {
                        case Success:
                            Log.d("TAG", "広告取得成功");

                            //TODO 3.広告をデータにマージする
                            mAdapter.addItems(mergeAdItems(items, adList));
                            break;
                        case Empty:
                            Log.d("TAG", "空広告");
                            break;
                        case Failure:
                        default:
                            Log.d("TAG", "広告取得失敗");
                    }
                    mTask = null;
                }
            });
        }
    }

    private List<MyItem> getMyItems() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("<yyyy/MM/dd hh:mm ss>");
        Date now = new Date();
        final List<MyItem> items = new ArrayList<MyItem>();
        for (int i = 0; i < 5; i++) {
            items.add(new MyItem("http://xxxx.yyy/thumbnail.png", "Title-" + i, "Desciption-" + i, "" + dateFormat.format(now)));
        }
        return items;
    }

    static class ItemViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        final List<Parcelable> mItems = new ArrayList<Parcelable>();
        final Context mContext;

        ItemViewAdapter(Context context) {
            mContext = context;
        }

        public void addItems(List<Parcelable> items) {
            mItems.addAll(items);
            notifyDataSetChanged();
        }

        public List<Parcelable> getItems() {
            return mItems;
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
            Parcelable item = mItems.get(position);
            AdItem adItem = null;
            if (item instanceof AdItem) {
                adItem = (AdItem) item;
                bindAdItem(vh, adItem);
            } else if (item instanceof MyItem) {
                bindMyItem(vh, (MyItem) item);
            }
            //TODO 4.広告ビューを監視する
            InfeedAd.setVisiblityTracking(mContext, vh.itemView, adItem);
        }

        private void bindAdItem(ItemViewHolder vh, final AdItem adItem) {
            //画像を設定
            Picasso.with(mContext).load(adItem.getImageUrl()).fit().into(vh.mImageView);
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
                    //TODO 5.クリック処理を行う
                    adItem.onClick(mContext);

                    //指定スキーム(単数)のクリック処理をハンドリングする
                    /*
                    adItem.onClickWithCustomScheme(mContext, "scheme1", new AdClickListener() {
                        @Override
                        public void onClick(String url) {
                            //ハンドリング
                        }
                    });
                    */

                    //指定スキーム(複数)のクリック処理をハンドリングする
                    /*
                    adItem.onClickWithCustomSchemes(mContext, new String[]{"scheme1", "scheme2", "scheme3"}, new AdClickListener() {
                        @Override
                        public void onClick(String url) {
                            //ハンドリング
                        }
                    });
                    */

                    //すべてのクリック処理をハンドリングする
                    /*
                    adItem.onClickWithHandler(mContext, new AdClickListener() {
                        @Override
                        public void onClick(String url) {
                            //ハンドリング
                        }
                    });
                    */
                }
            });
        }

        private void bindMyItem(ItemViewHolder vh, MyItem item) {
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

    static class MyItem implements Parcelable {
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

        protected MyItem(Parcel in) {
            mImageUrl = in.readString();
            mTitle = in.readString();
            mDescription = in.readString();
            mDate = in.readString();
        }

        public static final Creator<MyItem> CREATOR = new Creator<MyItem>() {
            @Override
            public MyItem createFromParcel(Parcel in) {
                return new MyItem(in);
            }

            @Override
            public MyItem[] newArray(int size) {
                return new MyItem[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mImageUrl);
            dest.writeString(mTitle);
            dest.writeString(mDescription);
            dest.writeString(mDate);
        }
    }

    public static List<Parcelable> mergeAdItems(List<MyItem> items, AdList adList) {
        List<Parcelable> result = new ArrayList<Parcelable>();

        List<AdItem> ads = adList.getAdItemList();
        Iterator<AdItem> adIterator = ads.iterator();
        int beginIndex = adList.getBeginIndex();
        int interval = adList.getInterval();

        int totalCount = items.isEmpty() ? 0 : items.size() + ads.size();
        Iterator<MyItem> itemIterator = items.iterator();
        for (int i = 0; i < totalCount; i++) {
            if (isAdPosition(i, beginIndex, interval) && adIterator.hasNext()) {
                result.add(adIterator.next());
            } else if (itemIterator.hasNext()) {
                result.add(itemIterator.next());
            }
        }

        return result;
    }

    public static boolean isAdPosition(int position, int beginIndex, int interval) {
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
