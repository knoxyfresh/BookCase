package temple.edu.bookcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import java.util.ArrayList;

public class pageActivity extends AppCompatActivity {
    FragmentManager fm;
    ViewPager vp;
    ArrayList<detailFragment> myfragments = new ArrayList<detailFragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        fm = getSupportFragmentManager();
        vp = findViewById(R.id.viewPager);
        detailFragment myfrag=new detailFragment();
        String[] arr= {"one","one"};
        myfragments.add(myfrag.newInstance(arr));
        myfrag=new detailFragment();
        String[] arr2= {"two","two"};
        myfragments.add(myfrag.newInstance(arr2));
        myfrag=new detailFragment();
        String[] arr3= {"three","three"};
        myfragments.add(myfrag.newInstance(arr3));
        myViewPagerAdapter adpt = new myViewPagerAdapter(fm, myfragments);
        vp.setAdapter(adpt);
        //set adapter full of fragments
//        set adapter of the viewpager
//      myfragments.Add(.newinstance etc);

    }

    class myViewPagerAdapter extends FragmentPagerAdapter{
        ArrayList<detailFragment> fragments;
        public myViewPagerAdapter(FragmentManager fm, ArrayList<detailFragment> frags) {
            super(fm);
            this.fragments = frags;
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
//        ArrayList<MyFragment>

    }
}
