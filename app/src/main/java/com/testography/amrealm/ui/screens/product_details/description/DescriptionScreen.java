package com.testography.amrealm.ui.screens.product_details.description;

import android.os.Bundle;

import com.testography.amrealm.R;
import com.testography.amrealm.data.storage.dto.DescriptionDto;
import com.testography.amrealm.data.storage.realm.ProductRealm;
import com.testography.amrealm.di.DaggerService;
import com.testography.amrealm.di.scopes.DaggerScope;
import com.testography.amrealm.flow.AbstractScreen;
import com.testography.amrealm.flow.Screen;
import com.testography.amrealm.mvp.models.DetailModel;
import com.testography.amrealm.mvp.presenters.AbstractPresenter;
import com.testography.amrealm.ui.screens.product_details.DetailScreen;

import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import mortar.MortarScope;

@Screen(R.layout.screen_description)
public class DescriptionScreen extends AbstractScreen<DetailScreen.Component> {
    private ProductRealm mProductRealm;

    public DescriptionScreen(ProductRealm productRealm) {
        mProductRealm = productRealm;
    }

    @Override
    public Object createScreenComponent(DetailScreen.Component parentComponent) {
        return DaggerDescriptionScreen_Component.builder()
                .component(parentComponent)
                .module(new Module())
                .build();
    }

    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(DescriptionScreen.class)
        DescriptionPresenter provideDescriptionPresenter() {
            return new DescriptionPresenter(mProductRealm);
        }
    }

    @dagger.Component(dependencies = DetailScreen.Component.class, modules =
            Module.class)
    @DaggerScope(DescriptionScreen.class)
    public interface Component {
        void inject(DescriptionPresenter presenter);

        void inject(DescriptionView view);
    }

    public class DescriptionPresenter extends AbstractPresenter<DescriptionView,
            DetailModel> {

        private final ProductRealm mProduct;
        private RealmChangeListener mListener;

        public DescriptionPresenter(ProductRealm productRealm) {
            mProduct = productRealm;
        }

        @Override
        protected void initActionBar() {
            // empty
        }

        @Override
        protected void initFab() {
            /*mRootPresenter.newFabBuilder()
                    .setVisible(View.VISIBLE)
                    .setIcon(R.drawable.ic_favorite_white_24dp)
                    .build();*/
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component) scope.getService(DaggerService
                    .SERVICE_NAME)).inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            getView().initView(new DescriptionDto(mProduct));

            mListener = element -> {
                if (getView() != null) {
                    getView().initView(new DescriptionDto(mProduct));
                }
            };
            mProduct.addChangeListener(mListener);
        }

        @Override
        public void dropView(DescriptionView view) {
            mProduct.removeChangeListener(mListener);
            super.dropView(view);
        }

        public void clickOnPlus() {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> mProduct.add());
            realm.close();
        }

        public void clickOnMinus() {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> mProduct.remove());
            realm.close();
        }
    }
}
