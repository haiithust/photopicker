package hai.ithust.PhotoPickerDemo;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hai.ithust.photopicker.R;
import hai.ithust.photopicker.holder.PhotoViewHolder;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    public static final int TYPE_ADD = 1;
    private static final int TYPE_PHOTO = 2;
    final static int MAX = 9;

    private ArrayList<String> mPhotoPaths = new ArrayList<>();
    private View.OnClickListener mClickListener;

    public PhotoAdapter(ArrayList<String> photoPath, View.OnClickListener listener) {
        mPhotoPaths.addAll(photoPath);
        mClickListener = listener;
    }

    public void setPhotoPaths(List<String> photoPaths) {
        mPhotoPaths.clear();
        if (photoPaths != null && !photoPaths.isEmpty()) {
            mPhotoPaths.addAll(photoPaths);
        }
        notifyDataSetChanged();
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case TYPE_ADD:
                itemView = LayoutInflater.from(parent.getContext()).inflate(hai.ithust.PhotoPickerDemo.R.layout.item_add, parent, false);
                itemView.setOnClickListener(mClickListener);
                break;
            case TYPE_PHOTO:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.picker_item_photo, parent, false);
                break;
        }
        return new PhotoViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_PHOTO) {
            Uri uri = Uri.fromFile(new File(mPhotoPaths.get(position)));
            Glide.with(holder.itemView.getContext())
                    .load(uri)
                    .centerCrop()
                    .into(holder.ivPhoto);
        }
    }


    @Override
    public int getItemCount() {
        int count = mPhotoPaths.size() + 1;
        if (count > MAX) {
            count = MAX;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mPhotoPaths.size() && position != MAX) ? TYPE_ADD : TYPE_PHOTO;
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private View vSelected;

        private PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            vSelected = itemView.findViewById(R.id.v_selected);
            if (vSelected != null) vSelected.setVisibility(View.GONE);
        }
    }

}
