package temple.edu.bookcase;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MyBookAdapter extends FragmentPagerAdapter {
    private int NUM_ITEMS;
    private ArrayList<Book> books;

    public MyBookAdapter(FragmentManager fm, ArrayList<Book> books) {
        super(fm);
        this.books = books;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
