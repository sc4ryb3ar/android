package com.bitlove.fetlife.view.adapter;

public abstract class ResourceListRecyclerAdapter<Resource, ResourceViewHolder extends SwipeableViewHolder> extends SwipeableRecyclerAdapter<ResourceViewHolder> {

    public abstract void refresh();

    public interface OnResourceClickListener<Resource> {
        void onItemClick(Resource resource);
        void onAvatarClick(Resource resource);
    }

    private OnResourceClickListener<Resource> onResourceClickListener;

    public void setOnItemClickListener(OnResourceClickListener<Resource> onConversationClickListener) {
        this.onResourceClickListener = onConversationClickListener;
    }

    public OnResourceClickListener<Resource> getOnItemClickListener() {
        return onResourceClickListener;
    }
}
