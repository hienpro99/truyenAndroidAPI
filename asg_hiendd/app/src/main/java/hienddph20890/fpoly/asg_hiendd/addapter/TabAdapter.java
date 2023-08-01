package hienddph20890.fpoly.asg_hiendd.addapter;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import hienddph20890.fpoly.asg_hiendd.fragment.CartFragment;
import hienddph20890.fpoly.asg_hiendd.fragment.HomeFragment;
import hienddph20890.fpoly.asg_hiendd.fragment.SearchFragment;
import hienddph20890.fpoly.asg_hiendd.fragment.UserFragment;

public class TabAdapter extends FragmentPagerAdapter {
    private Bundle userData; // Dữ liệu người dùng
    private boolean isAdmin;

    public TabAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public TabAdapter(FragmentManager fragmentManager, Bundle userData) {
        super(fragmentManager);
        this.userData = userData;
        if (userData != null) {
            this.isAdmin = userData.getBoolean("isAdmin", false);
        }
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                if (userData != null) {
                    // Chỉ đặt dữ liệu cho SearchFragment
                    fragment.setArguments(userData);
                }
                break;
            case 1:
                fragment = new SearchFragment();
                if (userData != null) {
                    // Chỉ đặt dữ liệu cho SearchFragment
                    fragment.setArguments(userData);
                }
                break;
            case 2:
                fragment = new CartFragment();
                if (userData != null) {
                    // Chỉ đặt dữ liệu cho SearchFragment
                    fragment.setArguments(userData);
                }
                break;
            case 3:
                fragment = new UserFragment();
                if (userData != null) {
                    // Chỉ đặt dữ liệu cho SearchFragment
                    fragment.setArguments(userData);
                }
                break;
        }

        // Nếu dữ liệu người dùng đã được đặt, chuyển nó xuống Fragment
        if (userData != null) {
            fragment.setArguments(userData);
        }

        return fragment;
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
