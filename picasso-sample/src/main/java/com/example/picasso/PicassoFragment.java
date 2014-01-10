package com.example.picasso;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

public class PicassoFragment extends DialogFragment {

  private String data[] = {
      "https://raw.github.com/square/picasso/master/website/static/sample.png",
      "https://raw.github.com/square/picasso/master/website/static/sample.png",
      "https://raw.github.com/square/picasso/master/website/static/sample.png",
      "https://raw.github.com/square/picasso/master/website/static/sample.png",
      "https://raw.github.com/square/picasso/master/website/static/sample.png",
      "https://raw.github.com/square/picasso/master/website/static/sample.png",
      "https://raw.github.com/square/picasso/master/website/static/sample.png",
      "https://raw.github.com/square/picasso/master/website/static/sample.png",
      "https://raw.github.com/square/picasso/master/website/static/sample.png",
      "https://raw.github.com/square/picasso/master/website/static/sample.png",
      "https://raw.github.com/square/picasso/master/website/static/sample.png",
      "https://raw.github.com/square/picasso/master/website/static/sample.png",
      "https://raw.github.com/square/picasso/master/website/static/sample.png",
  };

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity()).setTitle("Piccaso")
        .setAdapter(new ArrayAdapter<String>(getActivity(), 0, data) {

          @Override
          public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
              convertView = new ImageView(getActivity());
              convertView.setLayoutParams(
                  new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 100));
            }

            Picasso.with(getContext())
                .load(getItem(position))
                .fit()
                .centerInside() // Here is the problem
                .into((ImageView) convertView);

            return convertView;
          }
        }, null)
        .create();
  }
}