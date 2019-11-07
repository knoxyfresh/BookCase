package temple.edu.bookcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements BookChooserFragment.OnFragmentInteractionListener {

    FragmentManager fm;
    ViewPager vp;
    ArrayList<BookChooserFragment> myfragments = new ArrayList<BookChooserFragment>();
    String[] books;
    ArrayList<Book> mybooks = new ArrayList<Book>();
    FragmentPagerAdapter adapterViewPager;
    BookDetailsFragment detailsref;



    Handler loadBooks = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
                    Log.wtf("results",message.obj.toString());
            //Toast.makeText(MainActivity.this,(String)message.obj,Toast.LENGTH_LONG);
            try{
            JSONArray jarrary = new JSONArray(message.obj.toString());
            for(int i=0;i<jarrary.length();i++){
                Book book = new Book();
                JSONObject obj = jarrary.getJSONObject(i);
                book.id=obj.getInt("book_id");
                book.title=obj.getString("title");
                book.author=obj.getString("author");
                book.duration=obj.getInt("duration");
                book.published=obj.getInt("published");
                book.coverURL = new URL(obj.getString("cover_url"));
                mybooks.add(book);
                Log.wtf("results",mybooks.get(i).toString());
            }
            makeViewPager(mybooks);

//                BookDetailsFragment myfrag = BookDetailsFragment.newInstance(mybooks.get(0));
//                final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.mainlayout,myfrag);
//                transaction.commit();

            }catch(Exception ex){
                Log.wtf("results","PROBLEM OF "+ex.toString());

            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        int orientation = getResources().getConfiguration().orientation;

        Thread loadContent = new Thread(){

            @Override
            public void run() {
                URL url = null;
                try{
                    url = new URL("https://kamorris.com/lab/audlib/booksearch.php");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String response = "", tmpResponse;
                    tmpResponse = reader.readLine();
                    while(tmpResponse!=null){
                        response+=tmpResponse;
                        tmpResponse = reader.readLine();
                    }
                    Message msg = Message.obtain();
                    //Toast.makeText(MainActivity.this,"HI we have: "+response,Toast.LENGTH_LONG);
                    msg.obj=response;
                    loadBooks.sendMessage(msg);
                }catch(Exception ex){
                    //Toast.makeText(MainActivity.this,"WE HAVE A PROBLEM HOY HOY HOYHOYHOYHOYHOYHYOYH!",Toast.LENGTH_LONG);
                    Log.wtf("results","NOPE!");

                }
            }
        };
        loadContent.start();

//        boolean istablet = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
//        Log.wtf("OY","Tablert: "+istablet);
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE || istablet) {
//            books = getResources().getStringArray(R.array.Books);
//            // In landscape
//            detailsref = BookDetailsFragment.newInstance(books[0]);
//            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            BookChooserFragment myfrag = BookChooserFragment.newInstance(getResources().getStringArray(R.array.Books));
//            transaction.replace(R.id.selectFrameLand, myfrag);
//            transaction.replace(R.id.deatilsFrameLand, detailsref);
//            transaction.addToBackStack(null);
//            transaction.commit();
//        } else {
//            // In portrait
//            vp = findViewById(R.id.viewPagerMain);
//            ArrayList<String> strings = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Books)));
////        adapterViewPager = new this.MyPagerAdapter(getSupportFragmentManager(), strings);
//            adapterViewPager = new MainActivity.MyPagerAdapter(getSupportFragmentManager(), strings);
//            vp.setAdapter(adapterViewPager);
//        }

    }


    public boolean areWeConnected() {
        ConnectivityManager cmgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cmgr.getActiveNetworkInfo() != null && cmgr.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void ChooseItem(int i) {
//        Fragment myfrag = getSupportFragmentManager().findFragmentById(R.id.fragmentDetails);
//        ((BookDetailsFragment)myfrag).displayBook(books[i]);
//        detailsref.displayBook(books[i]);
        //vp.setCurrentItem(i, false);
    }

    public void makeViewPager(ArrayList<Book> books){
//        viewPagerFragment vpf = viewPagerFragment.newInstance(books);
//        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.mainlayout,vpf);
//        transaction.commit();
        BookChooserFragment frag = BookChooserFragment.newInstance(books);
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainlayout,frag);
        transaction.commit();


    }




//    public static class MyPagerAdapter extends FragmentPagerAdapter {
//        private int NUM_ITEMS;
//        private ArrayList<String> strings;
//
//        public MyPagerAdapter(FragmentManager fragmentManager, ArrayList<String> strings) {
//            super(fragmentManager);
//            NUM_ITEMS = strings.size();
//            this.strings = strings;
//        }
//
//        // Returns total number of pages
//        @Override
//        public int getCount() {
//            return NUM_ITEMS;
//        }
//
//        // Returns the fragment to display for that page
//        @Override
//        public Fragment getItem(int position) {
//            if (position < NUM_ITEMS)
//                return BookDetailsFragment.newInstance(strings.get(position));
//            else return null;
////            switch (position) {
////                case 0: // Fragment # 0 - This will show BookDetailsFragment
////                    return BookDetailsFragment.newInstance(0, "Page # 1");
////                case 1: // Fragment # 0 - This will show BookDetailsFragment different title
////                    return BookDetailsFragment.newInstance(1, "Page # 2");
////                case 2: // Fragment # 1 - This will show SecondFragment
////                    return BookDetailsFragment.newInstance(2, "Page # 3");
////                default:
////                    return null;
////            }
//        }
//
//        // Returns the page title for the top indicator
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return "Page " + position;
//        }
//
//    }




}
