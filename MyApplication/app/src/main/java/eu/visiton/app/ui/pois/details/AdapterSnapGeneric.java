package eu.visiton.app.ui.pois.details;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import eu.visiton.app.R;
import eu.visiton.app.materialx.utils.Tools;
import eu.visiton.app.model.Image;
import eu.visiton.app.util.Constantes;


public class AdapterSnapGeneric extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Image> items = new ArrayList<>();

    private OnLoadMoreListener onLoadMoreListener;

    private Context ctx;
    private int layout_id;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Image obj, int position);


    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterSnapGeneric(Context context, List<Image> items, int layout_id) {
        this.items = items;
        ctx = context;
        this.layout_id = layout_id;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView brief;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            name = v.findViewById(R.id.name);
            brief = v.findViewById(R.id.brief);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout_id, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Image obj = new Image();
        obj.image=items.get(position).image;
        obj.name =items.get(position).name;
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            view.name.setText(obj.name);
            view.brief.setText(obj.brief);


            Glide.with(ctx).load("http://10.0.2.2:9000/files/" + obj.image).into(view.image);
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showDialogImageFull(items.get(position));
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);

                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Image getItem(int position) {
        return items.get(position);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

    private void showDialogImageFull(Image item) {
        Toast.makeText(ctx, "BUeenos diias", Toast.LENGTH_SHORT).show();
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_image);

        // Button btnDownload =  dialog.findViewById(R.id.button_download);
        // onClickListener

        Button btn = null;
        btn.setOnClickListener(view -> {

        });

        Glide.with(ctx).load(item.image).into((ImageView) dialog.findViewById(R.id.ImageView_photo));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();
    }

}