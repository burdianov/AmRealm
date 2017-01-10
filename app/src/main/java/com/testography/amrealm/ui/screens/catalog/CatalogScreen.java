package com.testography.amrealm.ui.screens.catalog;

import android.content.Context;
import android.os.Bundle;

import com.squareup.picasso.Picasso;
import com.testography.amrealm.R;
import com.testography.amrealm.data.storage.realm.ProductRealm;
import com.testography.amrealm.di.DaggerService;
import com.testography.amrealm.di.scopes.DaggerScope;
import com.testography.amrealm.flow.AbstractScreen;
import com.testography.amrealm.flow.Screen;
import com.testography.amrealm.mvp.models.CatalogModel;
import com.testography.amrealm.mvp.presenters.AbstractPresenter;
import com.testography.amrealm.mvp.presenters.ICatalogPresenter;
import com.testography.amrealm.mvp.presenters.MenuItemHolder;
import com.testography.amrealm.mvp.presenters.RootPresenter;
import com.testography.amrealm.ui.activities.RootActivity;
import com.testography.amrealm.ui.screens.auth.AuthScreen;
import com.testography.amrealm.ui.screens.product.ProductScreen;

import dagger.Provides;
import flow.Flow;
import mortar.MortarScope;
import rx.Subscriber;
import rx.Subscription;

@Screen(R.layout.screen_catalog)
public class CatalogScreen extends AbstractScreen<RootActivity.RootComponent> {

    //region ==================== Flow & Mortar ===================

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerCatalogScreen_Component.builder()
                .rootComponent(parentComponent)
                .module(new Module())
                .build();
    }

    //endregion

    //region ==================== DI ===================

    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(CatalogScreen.class)
        CatalogModel provideCatalogModel() {
            return new CatalogModel();
        }

        @Provides
        @DaggerScope(CatalogScreen.class)
        CatalogPresenter provideCatalogPresenter() {
            return new CatalogPresenter();
        }
    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules =
            Module.class)
    @DaggerScope(CatalogScreen.class)
    public interface Component {
        void inject(CatalogPresenter presenter);
        void inject(CatalogView view);

        CatalogModel getCatalogModel();
        Picasso getPicasso();
        RootPresenter getRootPresenter();
    }

    //endregion

    //region ==================== Presenter ===================

    public class CatalogPresenter extends AbstractPresenter<CatalogView,
            CatalogModel> implements ICatalogPresenter {
        private int lastPagerPostion;

        //region ==================== Lifecycle ===================

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            mCompSubs.add(subscribeOnProductRealmObs());
        }

        @Override
        public void dropView(CatalogView view) {
            lastPagerPostion = getView().getCurrentPagerPosition();
            super.dropView(view);
        }

        @Override
        protected void initActionBar() {
            // custom view MenuItem
            mRootPresenter.newActionBarBuilder()
                    .setTitle("Catalog")
                    .addAction(new MenuItemHolder("To Cart", R.menu.menu_cart_counter_badge,
                            v -> {
                                getRootView().changeCart(R.menu.menu_cart_counter_badge);
                            }))
                    .build();

            // icon MenuItem
            /*mRootPresenter.newActionBarBuilder()
                    .setTitle("Catalog")
                    .addAction(new MenuItemHolder("To Cart", R.drawable
                            .ic_shopping_basket_black_24dp, item -> {
                        getRootView().showMessage("Go to Cart");
                        return true;
                    }))
                    .build();*/
        }

        @Override
        protected void initFab() {
            // empty
        }

        //endregion

        @Override
        public void clickOnBuyButton(int position) {
            if (getView() != null) {
                if (checkUserAuth() && getRootView() != null) {
                    getRootView().showMessage("Item " + mModel.getProductList()
                            .get(position).getProductName() +
                            " added successfully to the Cart");
                } else {
                    Flow.get(getView()).set(new AuthScreen());
                }
            }
        }

        private Subscription subscribeOnProductRealmObs() {
            if (getRootView() != null) {
                getRootView().showLoad();
            }
            return mModel.getProductObs()
                    .subscribe(new RealmSubscriber());
        }

        @Override
        public boolean checkUserAuth() {
            return mModel.isUserAuth();
        }

        private class RealmSubscriber extends Subscriber<ProductRealm> {
            CatalogAdapter mAdapter = getView().getAdapter();

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (getRootView() != null) {
                    getRootView().showError(e);
                }
            }

            @Override
            public void onNext(ProductRealm productRealm) {
                mAdapter.addItem(productRealm);
                if (mAdapter.getCount() - 1 == lastPagerPostion) {
                    getRootView().hideLoad();
                    getView().showCatalogView();
                }
            }
        }
    }

    //endregion

    public static class Factory {
        public static Context createProductContext(ProductRealm product, Context
                parentContext) {
            MortarScope parentScope = MortarScope.getScope(parentContext);
            MortarScope childScope = null;
            ProductScreen screen = new ProductScreen(product);

            String scopeName = String.format("%s_%s", screen.getScopeName(),
                    product.getId());

            if (parentScope.findChild(scopeName) == null) {
                childScope = parentScope.buildChild()
                        .withService(DaggerService.SERVICE_NAME, screen
                                .createScreenComponent(DaggerService
                                        .<CatalogScreen.Component>getDaggerComponent
                                                (parentContext)))
                        .build(scopeName);
            } else {
                childScope = parentScope.findChild(scopeName);
            }
            return childScope.createContext(parentContext);
        }
    }
}
