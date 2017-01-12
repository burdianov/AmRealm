package com.testography.amrealm.di.modules;

import com.testography.amrealm.data.managers.DataManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ModelModule {
    @Provides
    @Singleton
    DataManager privateDataManager() {
        return DataManager.getInstance();
    }
}
