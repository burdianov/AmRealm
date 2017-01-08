package com.testography.amrealm.di.components;

import com.testography.amrealm.data.managers.DataManager;
import com.testography.amrealm.di.modules.LocalModule;
import com.testography.amrealm.di.modules.NetworkModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(dependencies = AppComponent.class,
        modules = {NetworkModule.class, LocalModule.class})
@Singleton
public interface DataManagerComponent {
    void inject(DataManager dataManager);
}
