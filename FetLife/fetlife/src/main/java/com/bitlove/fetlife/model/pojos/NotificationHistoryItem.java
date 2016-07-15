package com.bitlove.fetlife.model.pojos;

import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = FetLifeDatabase.class)
public class NotificationHistoryItem extends BaseModel {

    @Column
    private String displayHeader;

    @Column
    private String displayMessage;

    @Column
    private String group;

    @Column
    private long validity;

    public String getDisplayHeader() {
        return displayHeader;
    }

    public void setDisplayHeader(String displayHeader) {
        this.displayHeader = displayHeader;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public long getValidity() {
        return validity;
    }

    public void setValidity(long validity) {
        this.validity = validity;
    }
}
