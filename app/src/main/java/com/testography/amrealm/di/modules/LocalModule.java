package com.testography.amrealm.di.modules;

import android.content.Context;

import com.testography.amrealm.data.managers.PreferencesManager;
import com.testography.amrealm.data.managers.RealmManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LocalModule {
    @Provides
    @Singleton
    PreferencesManager providePreferencesManager(Context context) {
        return new PreferencesManager(context);
    }

    @Provides
    @Singleton
    RealmManager provideRealmManager() {
        return new RealmManager();
    }
}
