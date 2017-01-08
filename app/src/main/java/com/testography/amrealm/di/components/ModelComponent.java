package com.testography.amrealm.di.components;

import com.testography.amrealm.di.modules.ModelModule;
import com.testography.amrealm.mvp.models.AbstractModel;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = ModelModule.class)
@Singleton
public interface ModelComponent {
    void inject(AbstractModel abstractModel);
}
