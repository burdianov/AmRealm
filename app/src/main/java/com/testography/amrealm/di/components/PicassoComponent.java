package com.testography.amrealm.di.components;

import com.squareup.picasso.Picasso;
import com.testography.amrealm.di.modules.PicassoCacheModule;
import com.testography.amrealm.di.scopes.RootScope;

import dagger.Component;

@Component(dependencies = AppComponent.class, modules = PicassoCacheModule.class)
@RootScope
public interface PicassoComponent {
    Picasso getPicasso();
}
