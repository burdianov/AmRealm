package com.testography.amrealm.mvp.models;

import com.testography.amrealm.data.managers.DataManager;
import com.testography.amrealm.di.DaggerService;
import com.testography.amrealm.di.components.DaggerModelComponent;
import com.testography.amrealm.di.components.ModelComponent;
import com.testography.amrealm.di.modules.ModelModule;

import javax.inject.Inject;

public abstract class AbstractModel {

    @Inject
    DataManager mDataManager;

    public AbstractModel() {
        ModelComponent component = DaggerService.getComponent(ModelComponent.class);
        if (component == null) {
            component = createDaggerComponent();
            DaggerService.registerComponent(ModelComponent.class, component);
        }
        component.inject(this);
    }

    private ModelComponent createDaggerComponent() {
        return DaggerModelComponent.builder()
                .modelModule(new ModelModule())
                .build();
    }
}