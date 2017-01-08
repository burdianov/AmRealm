package com.testography.amrealm.ui.screens.product_details.comments;

import android.support.annotation.NonNull;
import android.view.View;

import com.testography.amrealm.R;
import com.testography.amrealm.data.network.req.CommentReq;
import com.testography.amrealm.data.storage.realm.ProductRealm;
import com.testography.amrealm.di.DaggerService;
import com.testography.amrealm.di.scopes.DaggerScope;
import com.testography.amrealm.flow.AbstractScreen;
import com.testography.amrealm.flow.Screen;
import com.testography.amrealm.mvp.models.DetailModel;
import com.testography.amrealm.mvp.presenters.AbstractPresenter;
import com.testography.amrealm.mvp.presenters.MenuItemHolder;
import com.testography.amrealm.ui.screens.product_details.DetailScreen;

import dagger.Provides;
import flow.Flow;
import flow.TreeKey;
import mortar.MortarScope;

@Screen(R.layout.screen_add_comment)
public class AddCommentScreen extends AbstractScreen<DetailScreen.Component>
        implements TreeKey {

    private final ProductRealm mProductRealm;

    public AddCommentScreen(ProductRealm productRealm) {
        mProductRealm = productRealm;
    }

    @NonNull
    @Override
    public Object getParentKey() {
        return new CommentsScreen(mProductRealm);
    }

    //region ==================== DI ===================

    @Override
    public Object createScreenComponent(DetailScreen.Component parentComponent) {
        return DaggerAddCommentScreen_Component.builder()
                .component(parentComponent)
                .module(new AddCommentScreen.Module())
                .build();
    }

    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(AddCommentScreen.class)
        AddCommentPresenter provideAddCommentPresenter() {
            return new AddCommentPresenter(mProductRealm);
        }
    }

    @dagger.Component(dependencies = DetailScreen.Component.class, modules =
            Module.class)
    @DaggerScope(AddCommentScreen.class)
    public interface Component {
        void inject(AddCommentPresenter presenter);

        void inject(AddCommentView view);
    }

    //endregion

    //region ==================== Presenter ===================

    public class AddCommentPresenter extends AbstractPresenter<AddCommentView,
            DetailModel> {

        private final ProductRealm mProduct;

        public AddCommentPresenter(ProductRealm productRealm) {
            mProduct = productRealm;
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setTitle("Rate the Product")
                    .addAction(new MenuItemHolder("Save comment", R.drawable
                            .ic_done_orange_24dp, item -> {
                        getRootView().showMessage("Save Comment");
                        return true;
                    }))
                    .setBackArrow(true)
                    .build();
        }

        @Override
        protected void initFab() {
            mRootPresenter.newFabBuilder()
                    .setIcon(R.drawable.ic_add_white_24dp)
                    .setVisible(View.GONE)
                    .setOnClickListener(null)
                    .build();
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((AddCommentScreen.Component) scope.getService(DaggerService
                    .SERVICE_NAME)).inject(this);
        }

        public void clickOnSaveComment(CommentReq commentReq) {
            mModel.saveComment(mProduct.getId(), commentReq);
            Flow.get(getView()).goBack();
        }
    }

    //endregion
}
