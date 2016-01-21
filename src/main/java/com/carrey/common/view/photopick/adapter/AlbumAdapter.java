package com.carrey.common.view.photopick.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carrey.common.BaseAdapter;
import com.carrey.common.R;
import com.carrey.common.util.BitmapTools;
import com.carrey.common.view.photopick.AlbumModel;

import java.util.ArrayList;


/**
 * 专辑列表
 * AlbumAdapter
 * chenbo
 * 2015年3月16日 下午4:19:43
 * @version 1.0
 */
public class AlbumAdapter extends BaseAdapter<AlbumModel, AlbumAdapter.Holder> {
    
    private BitmapTools mBitmapTools;

	public AlbumAdapter(Context context, ArrayList<AlbumModel> models) {
		super(context, models);
		mBitmapTools = new BitmapTools(context);
		int w = (int)(context.getResources().getDimensionPixelOffset(R.dimen.albumitem_content_height) * 1.2);
//		mBitmapTools.configDefaultBitmapMaxSize(w, w);
	}

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewtype) {
        return new Holder(mInflater.inflate(R.layout.item_album, null));
    }


    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final AlbumModel album = mItems.get(position);
        holder.tvName.setText(album.getName());
        holder.tvCount.setText(album.getCount() + "张");
        holder.ivIndex.setVisibility(album.isCheck() ? View.VISIBLE : View.GONE);
        mBitmapTools.display(holder.ivAlbum, album.getRecent());
    }

    static class Holder extends RecyclerView.ViewHolder {
        public ImageView ivAlbum, ivIndex;
        public TextView tvName, tvCount;
        
        public Holder(View v) {
            super(v);
            
            ivAlbum = (ImageView) v.findViewById(R.id.iv_album_la);
            ivIndex = (ImageView) v.findViewById(R.id.iv_index_la);
            tvName = (TextView) v.findViewById(R.id.tv_name_la);
            tvCount = (TextView) v.findViewById(R.id.tv_count_la);
        }
    }


}
