package com.bitlove.fetlife.model.db;

import com.bitlove.fetlife.FetLifeApplication;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;

public class UserDataBaseDefinition extends DatabaseDefinition {

    private final DatabaseDefinition decoratedDbDefiniton;
    private final FetLifeApplication fetLifeApplication;

    public UserDataBaseDefinition(FetLifeApplication fetLifeApplication, DatabaseDefinition decoratedDbDefiniton) {
        this.fetLifeApplication = fetLifeApplication;
        this.decoratedDbDefiniton = decoratedDbDefiniton;
    }

    @Override
    public String getDatabaseName() {
        return decoratedDbDefiniton.getDatabaseName() + fetLifeApplication.getUser().getNickname();
    }

    @Override
    public boolean isInMemory() {
        return decoratedDbDefiniton.isInMemory();
    }

    @Override
    public int getDatabaseVersion() {
        return decoratedDbDefiniton.getDatabaseVersion();
    }

    @Override
    public boolean areConsistencyChecksEnabled() {
        return decoratedDbDefiniton.areConsistencyChecksEnabled();
    }

    @Override
    public boolean isForeignKeysSupported() {
        return decoratedDbDefiniton.isForeignKeysSupported();
    }

    @Override
    public boolean backupEnabled() {
        return decoratedDbDefiniton.backupEnabled();
    }

    @Override
    public Class<?> getAssociatedDatabaseClassFile() {
        return decoratedDbDefiniton.getAssociatedDatabaseClassFile();
    }
}
