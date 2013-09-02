package com.example.picasso;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SampleViewPagerActivity extends PicassoSampleActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sample_view_pager_activity);

    ViewPager pager = (ViewPager) findViewById(R.id.pager);
    pager.setAdapter(new ImagePagerAdapter1(this, Data.URLS));

  }

  public class ImagePagerAdapter1 extends PagerAdapter {
    // Declare Variables
    Context context;
    String[] images;
    String url;

    private final List<String> urls = new ArrayList<String>();

    final LayoutInflater inflater;

    public ImagePagerAdapter1(Context context, String[] images) {
      Collections.addAll(urls, images);
      this.context = context;
      this.images = images;
      this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
      return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      View itemView = inflater.inflate(R.layout.item_pager, container, false);

      // Locate the ImageView in viewpager_item.xml
      ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView1);
      ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);

      url = getItem(position);

      Picasso.with(context) //
          .load(url) //
          .error(R.drawable.error)
          .fit() //
          .into(imageView, new Callback() {
            @Override
            public void onSuccess() {
              Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
              Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
            }
          });

      // Add viewpager_item.xml to ViewPager
      container.addView(itemView);

      return itemView;
    }

    private String getItem(int paramInt) {
      return urls.get(paramInt);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      container.removeView((View) object);
    }
  }
}
