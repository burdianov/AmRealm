package com.testography.amrealm.di.components;

import android.content.Context;

import com.testography.amrealm.di.modules.AppModule;

import dagger.Component;

@Component(modules = AppModule.class)
public interface AppComponent {
    Context getContext();
}
