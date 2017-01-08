package com.testography.amrealm.di.modules;

import com.testography.amrealm.di.scopes.RootScope;
import com.testography.amrealm.mvp.models.AccountModel;
import com.testography.amrealm.mvp.presenters.RootPresenter;

import dagger.Provides;

@dagger.Module
public class RootModule {
    @Provides
    @RootScope
    RootPresenter provideRootPresenter() {
        return new RootPresenter();
    }

    @Provides
    @RootScope
    AccountModel provideAccountModel() {
        return new AccountModel();
    }
}
