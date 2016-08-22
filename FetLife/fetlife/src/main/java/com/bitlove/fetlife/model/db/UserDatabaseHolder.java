package com.bitlove.fetlife.model.db;

import com.bitlove.fetlife.FetLifeApplication;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.FetLifeDatabasefetlife_Database;
import com.raizlabs.android.dbflow.structure.Model;

public class UserDatabaseHolder extends DatabaseHolder {

    private DatabaseDefinition databaseDefinition;

    public UserDatabaseHolder() {
        FetLifeApplication.getInstance().setBaseDataBaseDefinition(new FetLifeDatabasefetlife_Database(this));
        databaseDefinition = new UserDataBaseDefinition();
    }

    @Override
    public DatabaseDefinition getDatabase(Class<?> databaseClass) {
        return databaseDefinition;
    }

    @Override
    public DatabaseDefinition getDatabase(String databaseName) {
        return databaseDefinition;
    }

    @Override
    public DatabaseDefinition getDatabaseForTable(Class<? extends Model> table) {
        return databaseDefinition;
    }

}
