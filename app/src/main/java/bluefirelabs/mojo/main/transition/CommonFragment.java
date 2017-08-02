package bluefirelabs.mojo.main.transition;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import bluefirelabs.mojo.R;

/**
 * Created by xmuSistone on 2016/9/18.
 */
public class CommonFragment extends Fragment implements DragLayout.GotoDetailListener {
    DragLayout dragLayout;
    private ImageView imageView, icon1, icon2, icon3, icon4;
    private View address1, address2, address3, address5;
    private TextView address4;
    private RatingBar ratingBar;
    /*private View head1, head2, head3, head4;*/
    private String imageUrl, description, restaurantName;
    private Integer restaurantColor;

    private Float rating = 0.0f;

    int defaultColor = 0x000000;
    int vibrantColor = -1, mutedColor = -1, mutedColor1 = -1, mutedColor2 = -1, mutedColor3 = -1, mutedColor4 = -1;
    int position;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //This sets up the view: inflates the general layout and then assigns the specific entities

        View rootView = inflater.inflate(R.layout.fragment_common, null);
        dragLayout = (DragLayout) rootView.findViewById(R.id.drag_layout);
        imageView = (ImageView) dragLayout.findViewById(R.id.image);

        /* Restaurant Indicator Icons */

        icon1 = (ImageView) dragLayout.findViewById(R.id.head1);
        icon2 = (ImageView) dragLayout.findViewById(R.id.head2);
        icon3 = (ImageView) dragLayout.findViewById(R.id.head3);
        icon4 = (ImageView) dragLayout.findViewById(R.id.head4);
        //imageView = (CircleImageView) dragLayout.findViewById(R.id.image);
        //ImageLoader.getInstance().displayImage(imageUrl, imageView);
        Picasso.with(getContext()).load(imageUrl).into(imageView);
        //Log.d("View Bound", imageUrl);


/*        icon1.setColorFilter(mutedColor1);
        icon2.setColorFilter(mutedColor2);
        icon3.setColorFilter(mutedColor3);
        icon4.setColorFilter(mutedColor4);*/


        //Use this to det dragLayout background colour
        /*Picasso.with(getContext())
                .load(imageUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        setColors(bitmap);
                    }
                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }
                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                }); */


        //setColors(imageView.getImageAlpha());
        //setColors(R.mipmap.ic_launcher_round);


        //imageView.setImageResource(R.drawable.image1);
        //address1 = dragLayout.findViewById(R.id.address1);
        //address2 = dragLayout.findViewById(R.id.address2);
        //address3 = dragLayout.findViewById(R.id.address3);
        address4 = (TextView) dragLayout.findViewById(R.id.address4);          //Restaurant Type
        address5 = dragLayout.findViewById(R.id.address5);
        ratingBar = (RatingBar) dragLayout.findViewById(R.id.rating);

        address4.setText(description);

        ratingBar.setMax(5);
        ratingBar.setStepSize(0.5f);
        ratingBar.setRating(rating);

/*        head1 = dragLayout.findViewById(R.id.head1);
        head2 = dragLayout.findViewById(R.id.head2);
        head3 = dragLayout.findViewById(R.id.head3);
        head4 = dragLayout.findViewById(R.id.head4);*/

        dragLayout.setGotoDetailListener(this);
        return rootView;
    }

    @Override
    public void gotoDetail() {
        Activity activity = (Activity) getContext();
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                new Pair(imageView, DetailActivity.IMAGE_TRANSITION_NAME)
                //new Pair(address1, DetailActivity.ADDRESS1_TRANSITION_NAME),
                //new Pair(address2, DetailActivity.ADDRESS2_TRANSITION_NAME),
                //new Pair(address3, DetailActivity.ADDRESS3_TRANSITION_NAME),
                //new Pair(address4, DetailActivity.ADDRESS4_TRANSITION_NAME),
               // new Pair(address5, DetailActivity.ADDRESS5_TRANSITION_NAME),
                //new Pair(ratingBar, DetailActivity.RATINGBAR_TRANSITION_NAME),
/*                new Pair(head1, DetailActivity.HEAD1_TRANSITION_NAME),
                new Pair(head2, DetailActivity.HEAD2_TRANSITION_NAME),
                new Pair(head3, DetailActivity.HEAD3_TRANSITION_NAME),
                new Pair(head4, DetailActivity.HEAD4_TRANSITION_NAME)*/
        );
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_IMAGE_URL, imageUrl);
        intent.putExtra(DetailActivity.EXTRA_RESTAURANT_DETAILS, description);
        intent.putExtra(DetailActivity.EXTRA_RESTAURANT_NAME, restaurantName);
        intent.putExtra(DetailActivity.EXTRA_RESTAURANT_COLOR, restaurantColor);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public void bindData(String imageUrl, String description, String restaurantName, Float rating) {
        this.imageUrl = imageUrl;
        this.description = description;
        this.restaurantName = restaurantName;
        this.rating = rating;
        Log.d("imageUrl", imageUrl);
    }

    public String dataReturn () {
        //Log.d("Return", this.imageUrl);
        return this.imageUrl;
    }

    public void bindColor (Integer restaurantColor){
        this.restaurantColor = restaurantColor;
    }

    public void setColors(Bitmap bitmap) {
        if (vibrantColor == -1 && mutedColor == -1) {
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), image);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    vibrantColor = palette.getVibrantColor(defaultColor);
                    mutedColor = palette.getMutedColor(defaultColor);
                    //obj.colorFetched(position, vibrantColor, mutedColor);
                    dragLayout.setBackgroundColor(vibrantColor);
                }
            });
        } else {
            dragLayout.setBackgroundColor(vibrantColor);
        }
    }

}