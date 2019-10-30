package temple.edu.bookcase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements BookChooserFragment.OnFragmentInteractionListener {

    FragmentManager fm;
    ViewPager vp;
    ArrayList<BookChooserFragment> myfragments = new ArrayList<BookChooserFragment>();
    String[] books;
    FragmentPagerAdapter adapterViewPager;
    BookDetailsFragment detailsref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        int orientation = getResources().getConfiguration().orientation;

        boolean istablet = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        Log.wtf("OY","Tablert: "+istablet);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE || istablet) {
            books = getResources().getStringArray(R.array.Books);
            // In landscape
            detailsref = BookDetailsFragment.newInstance(books[0]);
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BookChooserFragment myfrag = BookChooserFragment.newInstance(getResources().getStringArray(R.array.Books));
            transaction.replace(R.id.selectFrameLand, myfrag);
            transaction.replace(R.id.deatilsFrameLand, detailsref);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            // In portrait
            vp = findViewById(R.id.viewPagerMain);
            ArrayList<String> strings = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Books)));
//        adapterViewPager = new this.MyPagerAdapter(getSupportFragmentManager(), strings);
            adapterViewPager = new MainActivity.MyPagerAdapter(getSupportFragmentManager(), strings);
            vp.setAdapter(adapterViewPager);
        }

    }

    @Override
    public void ChooseItem(int i) {
//        Fragment myfrag = getSupportFragmentManager().findFragmentById(R.id.fragmentDetails);
//        ((BookDetailsFragment)myfrag).displayBook(books[i]);
        detailsref.displayBook(books[i]);
        //vp.setCurrentItem(i, false);
    }


    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS;
        private ArrayList<String> strings;

        public MyPagerAdapter(FragmentManager fragmentManager, ArrayList<String> strings) {
            super(fragmentManager);
            NUM_ITEMS = strings.size();
            this.strings = strings;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            if (position < NUM_ITEMS)
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
