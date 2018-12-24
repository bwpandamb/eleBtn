package com.charlesma;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.charlesma.elebtn.R;
import com.charlesma.elebtn.viewbtn.EleAddView;

public class CharlesMaListAdapter extends BaseQuickAdapter<FoodModel, BaseViewHolder> {

    public CharlesMaListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, final FoodModel item) {
        helper.setText(R.id.tv, item.getDesc());
        final EleAddView btnAdd = helper.getView(R.id.btn_add);
        btnAdd.setOnShoppingClickListener(new EleAddView.ShoppingClickListener() {
            @Override
            public void onAddClick(int num) {
                mShopOnClickListtener.add(btnAdd,getParentPosition(item));
                item.setNum(num);
            }

            @Override
            public void onMinusClick(int num) {
                mShopOnClickListtener.minus(btnAdd,getParentPosition(item));
                item.setNum(num);
            }
        });
        btnAdd.setTextNum(item.getNum());
    }

    private ShopOnClickListtener mShopOnClickListtener;

    public void setShopOnClickListtener(ShopOnClickListtener mShopOnClickListtener) {
        this.mShopOnClickListtener = mShopOnClickListtener;
    }


    public interface ShopOnClickListtener {

        void add(View view, int position);

        void minus(View view, int position);
    }

}
