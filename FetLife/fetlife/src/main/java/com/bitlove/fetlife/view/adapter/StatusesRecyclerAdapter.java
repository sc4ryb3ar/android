package com.bitlove.fetlife.view.adapter;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Status;
import com.bitlove.fetlife.model.pojos.StatusReference;
import com.bitlove.fetlife.model.pojos.StatusReference_Table;
import com.bitlove.fetlife.model.pojos.Status_Table;
import com.bitlove.fetlife.util.DateUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatusesRecyclerAdapter extends RecyclerView.Adapter<StatusViewHolder> {

    private final String memberId;
    private List<Status> itemList;

    public StatusesRecyclerAdapter(String memberId) {
        this.memberId = memberId;
        loadItems();
    }

    public void refresh() {
        loadItems();
        //TODO: think of possibility of update only specific items instead of the whole list
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        try {
            List<StatusReference> statusReferences = new Select().from(StatusReference.class).where(StatusReference_Table.userId.is(memberId)).orderBy(OrderBy.fromProperty(StatusReference_Table.id).ascending().collate(Collate.NOCASE)).queryList();
            List<String> statusIds = new ArrayList<>();
            for (StatusReference statusReference : statusReferences) {
                statusIds.add(statusReference.getId());
            }
            //TODO(profile):add proper ordering
            itemList = new Select().from(Status.class).where(Status_Table.id.in(statusIds)).orderBy(OrderBy.fromProperty(Status_Table.id).ascending().collate(Collate.NOCASE)).queryList();
        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }


    @Override
    public StatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_profile_status, parent, false);
        return new StatusViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StatusViewHolder holder, int position) {
        Status status = itemList.get(position);
        holder.statusText.setText(status.getBody());
        holder.statusDate.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(status.getDate())));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

class StatusViewHolder extends RecyclerView.ViewHolder {

    TextView statusText, statusDate;

    public StatusViewHolder(View itemView) {
        super(itemView);
        statusText = (TextView) itemView.findViewById(R.id.status_text);
        statusDate = (TextView) itemView.findViewById(R.id.status_date);
    }
}
