package com.bitlove.fetlife.model.db;

import com.bitlove.fetlife.FetLifeApplication;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;

public class UserDataBaseDefinition extends DatabaseDefinition {

    public UserDataBaseDefinition() {
    }

    @Override
    public String getDatabaseName() {
        return getDecoratedDbDefinition().getDatabaseName() + FetLifeApplication.getInstance().getUser().getNickname();
    }

    @Override
    public boolean isInMemory() {
        return getDecoratedDbDefinition().isInMemory();
    }

    @Override
    public int getDatabaseVersion() {
        return getDecoratedDbDefinition().getDatabaseVersion();
    }

    @Override
    public boolean areConsistencyChecksEnabled() {
        return getDecoratedDbDefinition().areConsistencyChecksEnabled();
    }

    @Override
    public boolean isForeignKeysSupported() {
        return getDecoratedDbDefinition().isForeignKeysSupported();
    }

    @Override
    public boolean backupEnabled() {
        return getDecoratedDbDefinition().backupEnabled();
    }

    @Override
    public Class<?> getAssociatedDatabaseClassFile() {
        return getDecoratedDbDefinition().getAssociatedDatabaseClassFile();
    }

    private DatabaseDefinition getDecoratedDbDefinition() {
        return FetLifeApplication.getInstance().getBaseDataBaseDefinition();
    }


}
