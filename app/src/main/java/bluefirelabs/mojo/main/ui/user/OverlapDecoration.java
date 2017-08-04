package bluefirelabs.mojo.main.ui.user;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by reza on 8/3/17.
 */

public class OverlapDecoration extends RecyclerView.ItemDecoration {

    private final static int overlap = -200;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        final int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == 0) {
            outRect.set(0, 0, 0, 0);
        } else {
            //outRect.set(overlap, 0, 0, 0);      //horizontal overlap
            outRect.set(0, overlap, 0, 0);      //vertical overlap
        }
    }
}
