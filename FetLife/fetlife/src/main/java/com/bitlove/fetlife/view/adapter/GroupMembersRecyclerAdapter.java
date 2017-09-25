package com.bitlove.fetlife.view.adapter;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.model.pojos.fetlife.db.GroupMemberReference;
import com.bitlove.fetlife.model.pojos.fetlife.db.RelationReference;
import com.bitlove.fetlife.model.pojos.fetlife.db.RelationReference_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member_Table;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GroupMembersRecyclerAdapter extends MembersRecyclerAdapter {

    private final String groupId;

    public GroupMembersRecyclerAdapter(String groupId, FetLifeApplication fetLifeApplication) {
        super(fetLifeApplication);
        this.groupId = groupId;
        loadItems();
    }

    protected void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        try {
            List<GroupMemberReference> relationReferences = null;//new Select().from(GroupMemberReference.class).where(GroupMemberReference_Table.userId.is(groupId)).orderBy(OrderBy.fromProperty(GroupMemberReference_Table.nickname).ascending().collate(Collate.UNICODE)).queryList();
            List<String> relationIds = new ArrayList<>();
            for (GroupMemberReference relationReference : relationReferences) {
                relationIds.add(relationReference.getId());
            }
            itemList = new Select().from(Member.class).where(Member_Table.id.in(relationIds)).orderBy(OrderBy.fromProperty(Member_Table.nickname).ascending().collate(Collate.UNICODE)).queryList();
            final Collator coll = Collator.getInstance();
            coll.setStrength(Collator.IDENTICAL);
            Collections.sort(itemList, new Comparator<Member>() {
                @Override
                public int compare(Member member, Member member2) {
                    //Workaround to match with DB sorting
                    String nickname1 = member.getNickname().replaceAll("_","z");
                    String nickname2 = member2.getNickname().replaceAll("_","z");
                    return coll.compare(nickname1,nickname2);
                }
            });
        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }
}