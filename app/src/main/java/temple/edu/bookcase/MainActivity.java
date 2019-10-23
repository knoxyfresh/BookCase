package temple.edu.bookcase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    detailFragment detailsfrag;
    FragmentManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.wtf("WTF!","You are in Landscape!!");
            Toast.makeText(MainActivity.this,"NOW LANDSCAPE!",Toast.LENGTH_LONG).show();
        }else{

        }
        fm = getSupportFragmentManager();
        String[] Books = getResources().getStringArray(R.array.Books);
        //books
        //ViewPager pager = findViewById(R.id.viewPager);
        detailsfrag = detailFragment.newInstance(getResources().getStringArray(R.array.Books));
        fm.beginTransaction().replace(R.id.frameLayout,detailsfrag).commit();
    }

}
