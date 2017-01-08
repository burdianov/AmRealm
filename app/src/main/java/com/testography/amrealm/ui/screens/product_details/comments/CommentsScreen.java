package com.testography.amrealm.ui.screens.product_details.comments;

import android.os.Bundle;

import com.testography.amrealm.R;
import com.testography.amrealm.data.storage.dto.CommentDto;
import com.testography.amrealm.data.storage.realm.CommentRealm;
import com.testography.amrealm.data.storage.realm.ProductRealm;
import com.testography.amrealm.di.DaggerService;
import com.testography.amrealm.di.scopes.DaggerScope;
import com.testography.amrealm.flow.AbstractScreen;
import com.testography.amrealm.flow.Screen;
import com.testography.amrealm.mvp.models.DetailModel;
import com.testography.amrealm.mvp.presenters.AbstractPresenter;
import com.testography.amrealm.ui.screens.product_details.DetailScreen;

import dagger.Provides;
import flow.Flow;
import io.realm.RealmList;
import mortar.MortarScope;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

@Screen(R.layout.screen_comments)
public class CommentsScreen extends AbstractScreen<DetailScreen.Component> {
    private ProductRealm mProductRealm;

    public CommentsScreen(ProductRealm productRealm) {
        mProductRealm = productRealm;
    }

    @Override
    public Object createScreenComponent(DetailScreen.Component parentComponent) {
        return DaggerCommentsScreen_Component.builder()
                .component(parentComponent)
                .module(new Module())
                .build();
    }

    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(CommentsScreen.class)
        CommentsPresenter provideCommentsPresenter() {
            return new CommentsPresenter(mProductRealm);
        }
    }

    @dagger.Component(dependencies = DetailScreen.Component.class,
            modules = Module.class)
    @DaggerScope(CommentsScreen.class)
    public interface Component {
        void inject(CommentsPresenter presenter);
        void inject(CommentsView view);
        void inject(CommentsAdapter adapter);
    }

    //region ==================== Presenter ===================

    public class CommentsPresenter extends AbstractPresenter<CommentsView,
            DetailModel> {

        private final ProductRealm mProduct;

        public CommentsPresenter(ProductRealm productRealm) {
            mProduct = productRealm;
        }

        @Override
        protected void initActionBar() {
            // empty
        }

        @Override
        protected void initFab() {
           /* mRootPresenter.newFabBuilder()
                    .setVisible(View.VISIBLE)
                    .setIcon(R.drawable.ic_add_white_24dp)
                    .build();*/
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);

            RealmList<CommentRealm> comments = mProduct.getCommentsRealm();
            Observable<CommentDto> commentsObs = Observable.from(comments)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .map(CommentDto::new);

            mCompSubs.add(subscribe(commentsObs, new ViewSubscriber<CommentDto>() {
                @Override
                public void onNext(CommentDto commentDto) {
                    getView().getAdapter().addItem(commentDto);
                }
            }));
            getView().initView();
        }

        public void clickOnAddComment() {
            if (getView() != null) {
                Flow.get(getView()).set(new AddCommentScreen(mProduct));
            }
        }
    }

    //endregion
}
