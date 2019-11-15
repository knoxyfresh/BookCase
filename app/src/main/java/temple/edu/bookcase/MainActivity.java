package temple.edu.bookcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BookChooserFragment.OnFragmentInteractionListener{

    FragmentManager fm;
    ViewPager vp;
    ArrayList<BookChooserFragment> myfragments = new ArrayList<BookChooserFragment>();
    String[] books;
    ArrayList<Book> mybooks = new ArrayList<Book>();
    FragmentPagerAdapter adapterViewPager;
    viewPagerFragment vpfrag;
    BookChooserFragment bcfrag;
    BookDetailsFragment detailsfrag;
    EditText searchbox;

    String urlMain = "https://kamorris.com/lab/audlib/booksearch.php";
    String urlSearch = "https://kamorris.com/lab/audlib/booksearch.php?search=";


    Handler loadBooks = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            Log.wtf("results", message.obj.toString());
            //Toast.makeText(MainActivity.this,(String)message.obj,Toast.LENGTH_LONG);
            try {
                JSONArray jarrary = new JSONArray(message.obj.toString());
                mybooks = new ArrayList<Book>();
                for (int i = 0; i < jarrary.length(); i++) {
                    Book book = new Book();
                    JSONObject obj = jarrary.getJSONObject(i);
                    book.id = obj.getInt("book_id");
                    book.title = obj.getString("title");
                    book.author = obj.getString("author");
                    book.duration = obj.getInt("duration");
                    book.published = obj.getInt("published");
                    book.coverURL = new URL(obj.getString("cover_url"));
                    mybooks.add(book);
                    Log.wtf("results", mybooks.get(i).toString());
                }
                //makeViewPager(mybooks);

//                BookDetailsFragment myfrag = BookDetailsFragment.newInstance(mybooks.get(0));
//                final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.mainlayout,myfrag);
//                transaction.commit();
                buildViews();

            } catch (Exception ex) {
                Log.wtf("results", "PROBLEM OF " + ex.toString());

            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        searchbox = findViewById(R.id.editTextMain);

        final Button searchbutton = findViewById(R.id.buttonSearchMain);
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDone(searchbox.getText().toString());
            }
        });



        getSupportActionBar().hide();
        //checking for old frags
        if (getSupportFragmentManager().findFragmentById(R.id.viewpagerFramePortrait) instanceof viewPagerFragment) {
            vpfrag = (viewPagerFragment) getSupportFragmentManager().findFragmentById(R.id.viewpagerFramePortrait);
            if (vpfrag.getBooks() != null) {
                mybooks = vpfrag.getBooks();
                Log.wtf("OKUR", "Found data " + mybooks);
                buildViews();
            } else {
                Log.wtf("OKUR", "Restored was null!");
                // do request
            }

        } else if (getSupportFragmentManager().findFragmentById(R.id.selectFrameLand) instanceof BookChooserFragment) {
            bcfrag = (BookChooserFragment) getSupportFragmentManager().findFragmentById(R.id.selectFrameLand);
            if (bcfrag.getBooks() != null) {
                mybooks = bcfrag.getBooks();
                Log.wtf("OKUR", "Found data " + mybooks);
                buildViews();
            } else {
                Log.wtf("OKUR", "Restored was null!");
                // do request
            }
        } else {
            getJSON(urlMain);

        }


//        boolean istablet = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
////        Log.wtf("OY","Tablert: "+istablet);
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE || istablet) {
//            makeViewPager(mybooks);
////            books = getResources().getStringArray(R.array.Books);
////            // In landscape
////            detailsref = BookDetailsFragment.newInstance(books[0]);
////            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
////            BookChooserFragment myfrag = BookChooserFragment.newInstance(getResources().getStringArray(R.array.Books));
////            transaction.replace(R.id.selectFrameLand, myfrag);
////            transaction.replace(R.id.deatilsFrameLand, detailsref);
////            transaction.addToBackStack(null);
////            transaction.commit();
//        } else {
//            makeListView(mybooks);
////            // In portrait
////            vp = findViewById(R.id.viewPagerMain);
////            ArrayList<String> strings = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Books)));
//////        adapterViewPager = new this.MyPagerAdapter(getSupportFragmentManager(), strings);
////            adapterViewPager = new MainActivity.MyPagerAdapter(getSupportFragmentManager(), strings);
////            vp.setAdapter(adapterViewPager);
//        }

    }


    public void buildViews() {
        int orientation = getResources().getConfiguration().orientation;
        boolean istablet = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE || istablet) {
            makeListView(mybooks);
        } else {
            makeViewPager(mybooks);
        }
    }

    public void getJSON(final String urltext) {
        Thread loadContent = new Thread() {

            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(urltext);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String response = "", tmpResponse;
                    tmpResponse = reader.readLine();
                    while (tmpResponse != null) {
                        response += tmpResponse;
                        tmpResponse = reader.readLine();
                    }
                    Message msg = Message.obtain();
                    //Toast.makeText(MainActivity.this,"HI we have: "+response,Toast.LENGTH_LONG);
                    msg.obj = response;
                    loadBooks.sendMessage(msg);
                } catch (Exception ex) {
                    //Toast.makeText(MainActivity.this,"WE HAVE A PROBLEM HOY HOY HOYHOYHOYHOYHOYHYOYH!",Toast.LENGTH_LONG);
                    Log.wtf("results", "NOPE!");

                }
            }
        };
        loadContent.start();
    }


    public boolean areWeConnected() {
        ConnectivityManager cmgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cmgr.getActiveNetworkInfo() != null && cmgr.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void ChooseItem(int i) {
        detailsfrag.changeBook(mybooks.get(i));
    }


    public void searchDone(String searchtext){
        Log.wtf("edittext",searchtext);
        if(searchtext!="")
        getJSON(urlSearch+searchtext);
        else getJSON(urlMain);
    }

    public void makeViewPager(ArrayList<Book> books) {
        viewPagerFragment vpf = viewPagerFragment.newInstance(books);
//        viewPagerFragment vpf = viewPagerFragment.newInstance(books, 2);
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.viewpagerFramePortrait, vpf);
        transaction.commit();
    }

    public void makeListView(ArrayList<Book> books) {
        BookChooserFragment frag = BookChooserFragment.newInstance(books);
        BookDetailsFragment details = BookDetailsFragment.newInstance(books.get(0));
        detailsfrag = details;
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.selectFrameLand, frag);
        transaction.replace(R.id.deatilsFrameLand, details);
        transaction.commit();
    }


}
