package com.example.mywebbuilder.editor;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywebbuilder.R;
import com.example.mywebbuilder.models.ComponentModel;
import com.google.gson.Gson;

import java.util.List;

public class EditorRVAdapter extends RecyclerView.Adapter<EditorRVAdapter.ViewHolder> {
    Context context;
    List<ComponentModel> list;
    DrawerListener drawerListener;
    public boolean isCheckBoxVisible = false;
    public boolean isSelectedAll = false;

    public EditorRVAdapter(Context context, List<ComponentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public EditorRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.editor_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetJavaScriptEnabled", "NotifyDataSetChanged", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull EditorRVAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.webView.getSettings().setAllowFileAccess(true);
        holder.webView.getSettings().setAllowFileAccessFromFileURLs(true);
        holder.webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(position == list.size()-1) ((EditorActivity)context).setProgressBarGone();
            }
        });

        if (isCheckBoxVisible) holder.selectedShadow.setVisibility(View.VISIBLE);
        else holder.selectedShadow.setVisibility(View.GONE);

        if(isSelectedAll || ((EditorActivity) context).selectedList.contains(position)) holder.checkbox.setImageResource(R.drawable.ic_check_box_24);
        else holder.checkbox.setImageResource(R.drawable.ic_check_box_blank_24);

        holder.webView.loadUrl(list.get(position).getPreviewUrl());
        holder.webView.setOnTouchListener(new View.OnTouchListener() {
            private final GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    try {
                        if(!((EditorActivity) context).selectedList.isEmpty()){
                            if(((EditorActivity) context).selectedList.contains(position)){
                                isSelectedAll = false;
                                ((EditorActivity) context).selectedList.remove(Integer.valueOf(position));
                                ((EditorActivity) context).selectItem(position, -1);
                            }else{
                                ((EditorActivity) context).selectedList.add(position);
                                ((EditorActivity) context).selectItem(position, 1);
                            }
                            notifyItemChanged(position);
                        }
                    }catch (Exception exp){
                        Toast.makeText(context, ""+exp, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }

                @Override
                public void onLongPress(@NonNull MotionEvent e) {
                    if(((EditorActivity) context).selectedList.isEmpty()){
                        isCheckBoxVisible = true;
                        ((EditorActivity) context).selectedList.add(position);
                        ((EditorActivity) context).selectItem(position, 1);
                        notifyDataSetChanged();
                    }
                    super.onLongPress(e);
                }
            });
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addComponent(ComponentModel component, int position) {
        list.add(position, component);
        notifyItemInserted(position);
    }

    public void setOnDragStartListener(DrawerListener listener) {
        this.drawerListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnDragListener {
        WebView webView;
        ImageView checkbox;
        ConstraintLayout addLine, selectedShadow;
        FrameLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            webView = itemView.findViewById(R.id.web_view);
            addLine = itemView.findViewById(R.id.add_line);
            mainLayout = itemView.findViewById(R.id.main_layout);
            selectedShadow = itemView.findViewById(R.id.select_shadow);
            checkbox = itemView.findViewById(R.id.check_box);
            webView.setOnDragListener(this);
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (drawerListener != null) drawerListener.onDragsStarted();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    addLine.setVisibility(View.VISIBLE);
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    addLine.setVisibility(View.GONE);
                    return true;
                case DragEvent.ACTION_DROP:
                    addLine.setVisibility(View.GONE);
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String jsonString = item.getText().toString();
                    ComponentModel component = new Gson().fromJson(jsonString, ComponentModel.class);
                    int targetPosition = getAdapterPosition();
                    ((EditorActivity) context).addComponentToRecyclerView(component, targetPosition + 1);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    return true;
            }
            return false;
        }

    }
}
