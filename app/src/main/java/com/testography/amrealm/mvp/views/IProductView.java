package com.testography.amrealm.mvp.views;

import com.testography.amrealm.data.storage.dto.ProductDto;

public interface IProductView extends IView {
    void showProductView(ProductDto product);
    void updateProductCountView(ProductDto product);

}
