package temple.edu.bookcase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    FragmentManager fm;
    ViewPager vp;
    ArrayList<BookChooserFragment> myfragments = new ArrayList<BookChooserFragment>();
    FragmentPagerAdapter adapterViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vp = findViewById(R.id.viewPagerMain);
        ArrayList<String> strings = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Books)));
//        adapterViewPager = new this.MyPagerAdapter(getSupportFragmentManager(), strings);
        adapterViewPager = new MainActivity.MyPagerAdapter(getSupportFragmentManager(), strings);
        vp.setAdapter(adapterViewPager);

    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS;
        private ArrayList<String> strings;

        public MyPagerAdapter(FragmentManager fragmentManager, ArrayList<String> strings) {
            super(fragmentManager);
            NUM_ITEMS = strings.size();
            this.strings=strings;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            if(position<NUM_ITEMS)
                return BookDetailsFragment.newInstance(strings.get(position));
            else return null;
//            switch (position) {
//                case 0: // Fragment # 0 - This will show BookDetailsFragment
//                    return BookDetailsFragment.newInstance(0, "Page # 1");
//                case 1: // Fragment # 0 - This will show BookDetailsFragment different title
//                    return BookDetailsFragment.newInstance(1, "Page # 2");
//                case 2: // Fragment # 1 - This will show SecondFragment
//                    return BookDetailsFragment.newInstance(2, "Page # 3");
//                default:
//                    return null;
//            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }


}
