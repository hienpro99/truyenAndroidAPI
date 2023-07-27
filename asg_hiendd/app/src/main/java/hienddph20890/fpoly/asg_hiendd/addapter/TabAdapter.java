package hienddph20890.fpoly.asg_hiendd.addapter;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import hienddph20890.fpoly.asg_hiendd.fragment.CartFragment;
import hienddph20890.fpoly.asg_hiendd.fragment.HomeFragment;
import hienddph20890.fpoly.asg_hiendd.fragment.SearchFragment;
import hienddph20890.fpoly.asg_hiendd.fragment.UserFragment;

public class TabAdapter extends FragmentPagerAdapter {

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new CartFragment();
            case 3:
                return new UserFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4; // Number of tabs
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable icon = null;
        switch (position) {
            case 0:
                return "Trang Chủ";
            case 1:
                return "Tìm Kiếm";
            case 2:
                return "Giỏ Hàng";
            case 3:
                return "Tài Khoản";
            default:
                return "";
        }
    }
}
