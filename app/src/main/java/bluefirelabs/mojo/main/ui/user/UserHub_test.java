package bluefirelabs.mojo.main.ui.user;

import android.animation.ArgbEvaluator;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.handlers.adapters.FirebaseRecyclerAdapterRestaurants;
import bluefirelabs.mojo.handlers.adapters.Food_List;
import bluefirelabs.mojo.main.transition.CommonFragment;
import bluefirelabs.mojo.main.transition.CustPagerTransformer;


/**
 * Created by xmuSistone on 2016/9/18.
 */
public class UserHub_test extends FragmentActivity {


    ArgbEvaluator argbEvaluator;
    int defaultColor = 0x000000;
    int vibrantColor = -1, mutedColor = -1;

    FirebaseAuth firebaseAuth;
    public static final String RESTAURANT = "listing";
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Food_List, FirebaseRecyclerAdapterRestaurants.RecyclerViewHolder> mFirebaseAdapter;

    private TextView indicatorTv;
    private View positionView;
    private ViewPager viewPager;
    private List<CommonFragment> fragments = new ArrayList<>(); // 供ViewPager使用
    //private final String[] imageArray = {"https://api.just-eat.ca/images/en-CA/cuisine/Indian/banner?width=1024&quality=50", "https://api.just-eat.ca/images/en-CA/cuisine/Indian/banner?width=1024&quality=50", "https://api.just-eat.ca/images/en-CA/cuisine/Indian/banner?width=1024&quality=50", "https://api.just-eat.ca/images/en-CA/cuisine/Indian/banner?width=1024&quality=50", "https://api.just-eat.ca/images/en-CA/cuisine/Indian/banner?width=1024&quality=50", "https://api.just-eat.ca/images/en-CA/cuisine/Indian/banner?width=1024&quality=50"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // 1. 沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        argbEvaluator = new ArgbEvaluator();
        positionView = findViewById(R.id.position_view);
        dealStatusBar(); // 调整状态栏高度

        // 2. 初始化ImageLoader
        //initImageLoader();

        // 3. 填充ViewPager
        fillViewPager();
    }

    /**
     * 填充ViewPager
     */

    public void firebaseTask(final MyCallback myCallback) {

        myCallback.callbackCall("listing");
    }

    public void backgroundChanger(final MyCallback_2 myCallback_2, String imageURL) {

        myCallback_2.callbackCall(imageURL);
    }

    private void fillViewPager() {
        indicatorTv = (TextView) findViewById(R.id.indicator_tv);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // 1. viewPager添加parallax效果，使用PageTransformer就足够了
        viewPager.setPageTransformer(false, new CustPagerTransformer(this));

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();



        MyCallback_2 myCallback_2 = new MyCallback_2() {

            public void setColors_fromCallback(Bitmap bitmap) {
                if (vibrantColor == -1 && mutedColor == -1) {
                    //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), image);
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            vibrantColor = palette.getVibrantColor(defaultColor);
                            mutedColor = palette.getMutedColor(defaultColor);
                            //obj.colorFetched(position, vibrantColor, mutedColor);
                            viewPager.setBackgroundColor(vibrantColor);
                        }
                    });
                } else {
                    viewPager.setBackgroundColor(vibrantColor);
                }
            }

            @Override
            public void callbackCall(String imageURL) {
                Picasso.with(getApplicationContext())
                        .load(imageURL)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                setColors_fromCallback(bitmap);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }


                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            }

        };


        //---------------------------------------------------------------------------------//


        final MyCallback myCallback = new MyCallback() {
            @Override
            public void callbackCall(final String restaurant) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(restaurant).child("venue");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final Map<String, Object> hopperValues = (Map<String, Object>) dataSnapshot.getValue();

                        //Log.d("Size", String.valueOf(hopperValues.size()));

                        if (hopperValues != null) {

                            Log.d("Size", String.valueOf(hopperValues.size()));
                            Log.d("Keys", String.valueOf(hopperValues.keySet()));

                            // 2. viewPager添加adapter
                            for (int i = 0; i < hopperValues.size(); i++) {       //This is the list of menu items
                                // 预先准备10个fragment
                                fragments.add(new CommonFragment());
                            }


                            viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
                                @Override
                                public Fragment getItem(int position) {
                                    final Map<String, Object> restaurantIcons = (Map<String, Object>) dataSnapshot.getValue();
                                    CommonFragment fragment = fragments.get(position % 10);
                                    Log.d("Position", String.valueOf(position));


                                    fragment.bindData(dataSnapshot.child("id" + String.valueOf(position)).child("icon").getValue().toString());
                                    //backgroundChanger(myCallback_2, dataSnapshot.child("id" + String.valueOf(position)).child("icon").getValue().toString());
                                    return fragment;
                                }

                                @Override
                                public int getCount() {
                                    return hopperValues.size();
                                }       //This is the number of restaurants
                            });


                            // 3. viewPager滑动时，调整指示器
                            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                    //backgroundChanger(myCallback_2, fragments.get(position%10).dataReturn());
                                    //Log.d("URL", fragments.get(position%10).dataReturn());


                                    Picasso.with(getApplicationContext())
                                            .load(fragments.get(position%10).dataReturn())
                                            .into(new Target() {
                                                @Override
                                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                    //Log.d("Setting Colour for", fragments.get(position%10).dataReturn());
                                                    Log.d("Changing", "activated 1");

                                                        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), image);
                                                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                                            @Override
                                                            public void onGenerated(Palette palette) {
                                                                vibrantColor = palette.getVibrantColor(defaultColor);
                                                                mutedColor = palette.getMutedColor(defaultColor);
                                                                Log.d("Changing", "activated 2");
                                                                //obj.colorFetched(position, vibrantColor, mutedColor);
                                                                viewPager.setBackgroundColor(vibrantColor);
                                                            }
                                                        });
                                                        /*Palette palette;
                                                        palette = Palette.from(bitmap).generate();
                                                        vibrantColor = palette.getVibrantColor(defaultColor);
                                                        mutedColor = palette.getMutedColor(defaultColor);
                                                        Log.d("Changing", "activated 2");
                                                        //obj.colorFetched(position, vibrantColor, mutedColor);
                                                        viewPager.setBackgroundColor(vibrantColor);
                                                        */

                                                }

                                                @Override
                                                public void onBitmapFailed(Drawable errorDrawable) {

                                                }


                                                @Override
                                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                }
                                            });

                                        Log.d("Test", fragments.get(position%10).dataReturn());


                                    //TODO: Add the bitmap colour url to firebase

                                    /* if (position < hopperValues.size() - 1) {                                               //animates the colour
                                        viewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset,
                                                Color.BLACK, Color.BLUE));
                                    } */

                                    /*
                                    if(position == 1){
                                        viewPager.setBackgroundColor(Color.CYAN);
                                    } else {
                                        Picasso.with(getApplicationContext())
                                                .load(fragments.get(position%10).dataReturn())
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
                                                });
                                    } */


                                }

                                @Override
                                public void onPageSelected(int position) {
                                    updateIndicatorTv();
                                }

                                @Override
                                public void onPageScrollStateChanged(int state) {

                                }
                            });

                            updateIndicatorTv();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        firebaseTask(myCallback);
    }

    /**
     * 更新指示器
     */
    private void updateIndicatorTv() {
        int totalNum = viewPager.getAdapter().getCount();
        int currentItem = viewPager.getCurrentItem() + 1;
        indicatorTv.setText(Html.fromHtml("<font color='#12edf0'>" + currentItem + "</font>  /  " + totalNum));
    }

    /**
     * 调整沉浸式菜单的title
     */
    private void dealStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = getStatusBarHeight();
            ViewGroup.LayoutParams lp = positionView.getLayoutParams();
            lp.height = statusBarHeight;
            positionView.setLayoutParams(lp);
        }
    }

    private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    public void setColors(Bitmap bitmap) {
        if (vibrantColor == -1 && mutedColor == -1) {
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), image);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    vibrantColor = palette.getVibrantColor(defaultColor);
                    mutedColor = palette.getMutedColor(defaultColor);
                    Log.d("Changing", "activated");
                    //obj.colorFetched(position, vibrantColor, mutedColor);
                    viewPager.setBackgroundColor(vibrantColor);
                }
            });
        } else {
            viewPager.setBackgroundColor(vibrantColor);
        }
    }

}
