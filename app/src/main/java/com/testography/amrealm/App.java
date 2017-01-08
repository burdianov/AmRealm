package com.testography.amrealm;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.stetho.Stetho;
import com.testography.amrealm.di.DaggerService;
import com.testography.amrealm.di.components.AppComponent;
import com.testography.amrealm.di.components.DaggerAppComponent;
import com.testography.amrealm.di.modules.AppModule;
import com.testography.amrealm.di.modules.PicassoCacheModule;
import com.testography.amrealm.di.modules.RootModule;
import com.testography.amrealm.mortar.ScreenScoper;
import com.testography.amrealm.ui.activities.DaggerRootActivity_RootComponent;
import com.testography.amrealm.ui.activities.RootActivity;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

public class App extends Application {
    private static SharedPreferences sSharedPreferences;
    private static Context sAppContext;
    private static Context sContext;
    private static AppComponent sAppComponent;
    private static RootActivity.RootComponent sRootActivityRootComponent;

    private MortarScope mRootScope;
    private MortarScope mRootActivityScope;

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

    @Override
    public Object getSystemService(String name) {
        return mRootScope.hasService(name) ? mRootScope.getService(name) : super
                .getSystemService(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                .build());

        createAppComponent();
        createRootActivityComponent();

        sContext = getApplicationContext();

        mRootScope = MortarScope.buildRootScope()
                .withService(DaggerService.SERVICE_NAME, sAppComponent)
                .build("Root");

        mRootActivityScope = mRootScope.buildChild()
                .withService(DaggerService.SERVICE_NAME, sRootActivityRootComponent)
                .withService(BundleServiceRunner.SERVICE_NAME, new BundleServiceRunner())
                .build(RootActivity.class.getName());

        ScreenScoper.registerScope(mRootScope);
        ScreenScoper.registerScope(mRootActivityScope);

        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sAppContext = getApplicationContext();
    }

    private void createAppComponent() {
        sAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(getApplicationContext()))
                .build();
    }

    private void createRootActivityComponent() {
        sRootActivityRootComponent = DaggerService.createComponent(RootActivity
                        .RootComponent.class, DaggerRootActivity_RootComponent.class,
                getAppComponent(),
                new PicassoCacheModule(),
                new RootModule());
    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static RootActivity.RootComponent getRootActivityRootComponent() {
        return sRootActivityRootComponent;
    }

    public static Context getContext() {
        return sContext;
    }
}
