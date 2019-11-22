package temple.edu.bookcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity implements BookChooserFragment.OnFragmentInteractionListener, viewPagerFragment.chooseListener {

    ArrayList<Book> mybooks = new ArrayList<Book>();
    FragmentPagerAdapter adapterViewPager;
    viewPagerFragment vpfrag;
    BookChooserFragment bcfrag;
    BookDetailsFragment detailsfrag;
    EditText searchbox;
    SeekBar mySeekBar;
    Book currentBook = null;
    int currentBookIndex;
    int playingbookindex;
    boolean paused = false;
    boolean progressrestored = false;

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

    AudiobookService.MediaControlBinder myServiceMediaBinder;

    Handler serviceHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            Log.wtf("msg", message.obj.toString());
            return false;
        }
    });

    Handler progressHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            int progress = mySeekBar.getProgress();
            Log.wtf("Progress", Integer.toString(progress));
//            mySeekBar.setMax(60);
            mySeekBar.setProgress(progress + 1);
            if (progress == currentBook.duration) {
                myServiceMediaBinder.stop();
            }
            return false;
        }
    });


    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, AudiobookService.class);
        bindService(serviceIntent, myConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!isChangingConfigurations()){
        unbindService(myConnection);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("book", currentBook);
        outState.putInt("playingindex", playingbookindex);
        outState.putInt("bookindex", currentBookIndex);
        outState.putInt("progress", mySeekBar.getProgress());
        outState.putBoolean("paused", paused);
        outState.putParcelableArrayList("booklist",mybooks);
            Log.wtf("OKUR", "SAVINGBOOKS data " + mybooks);
        progressrestored = true;
//        outState.putParcelable("service",myServiceMediaBinder);
    }

    ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            AudiobookService.MediaControlBinder binder = (AudiobookService.MediaControlBinder) service;
            myServiceMediaBinder = binder;
            //binder.play(1);
            binder.setProgressHandler(progressHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        mySeekBar = findViewById(R.id.seekBar);
        mySeekBar.setVisibility(View.INVISIBLE); //start off as hidden
        if (savedInstanceState != null) {
            //restore info about state
            currentBook = savedInstanceState.getParcelable("book");
            TextView np = findViewById(R.id.textViewNowPlaying);
            if(currentBook!=null){
            np.setText("Playing: "+currentBook.title);
            mySeekBar.setMax(currentBook.duration);
            mySeekBar.setVisibility(View.VISIBLE);
            mySeekBar.setProgress(savedInstanceState.getInt("progress"));
            }
            playingbookindex = savedInstanceState.getInt("playingindex");
            currentBookIndex = savedInstanceState.getInt("bookindex");
            paused = savedInstanceState.getBoolean("paused");

            mybooks=savedInstanceState.getParcelableArrayList("booklist");
            buildViews();
            Log.wtf("OKUR","Here are my books: "+mybooks);
            //checking for old frags
        }else{
                getJSON(urlMain);
        }

        searchbox = findViewById(R.id.editTextMain);


        final Button searchbutton = findViewById(R.id.buttonSearchMain);
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDone(searchbox.getText().toString());
            }
        });

        final Button playbutton = findViewById(R.id.buttonPlay);
        playbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playCurrentBook();
            }
        });

        final Button stopbutton = findViewById(R.id.buttonStop);
        stopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopCurrentBook();
            }
        });

        final Button pausebutton = findViewById(R.id.buttonPause);
        pausebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseCurrentBook();
            }
        });


        mySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (myServiceMediaBinder != null) {
                    //Log.wtf("msg",progress+"second of Length: "+currentBook.duration+" index:"+currentBookIndex);
                    playCurrentBook();
                }

            }
        });


        getSupportActionBar().hide();
//        //checking for old frags
//        if (getSupportFragmentManager().findFragmentById(R.id.viewpagerFramePortrait) instanceof viewPagerFragment) {
//            vpfrag = (viewPagerFragment) getSupportFragmentManager().findFragmentById(R.id.viewpagerFramePortrait);
//            if (vpfrag.getBooks() != null) {
//                mybooks = vpfrag.getBooks();
//                Log.wtf("OKUR", "Found data " + mybooks);
//                buildViews();
//            } else {
//                Log.wtf("OKUR", "Restored was null!");
//                // do request
//            }
//
//        } else if (getSupportFragmentManager().findFragmentById(R.id.selectFrameLand) instanceof BookChooserFragment) {
//            bcfrag = (BookChooserFragment) getSupportFragmentManager().findFragmentById(R.id.selectFrameLand);
//            if (bcfrag.getBooks() != null) {
//                mybooks = bcfrag.getBooks();
//                Log.wtf("OKUR", "Found data " + mybooks);
//                buildViews();
//            } else {
//                Log.wtf("OKUR", "Restored was null!");
//                // do request
//            }
//        } else {
//            getJSON(urlMain);
//
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

    public void getCurrentBook(int id) {
        currentBookIndex = mybooks.get(id).id;
        currentBook = mybooks.get(id);
    }


    public void playCurrentBook() {
        mySeekBar.setVisibility(View.VISIBLE);
        if (currentBookIndex == 0) getCurrentBook(0);

        if (paused) {
            myServiceMediaBinder.play(playingbookindex, mySeekBar.getProgress());
            paused = false;
        } else if (currentBookIndex != playingbookindex) {
            myServiceMediaBinder.stop();
            mySeekBar.setMax(currentBook.duration);
            if (progressrestored) {
                progressrestored = false;
            } else {
                mySeekBar.setProgress(0);
            }
            TextView np = findViewById(R.id.textViewNowPlaying);
            np.setText("Playing: " + currentBook.title);
            myServiceMediaBinder.play(currentBookIndex);
            playingbookindex = currentBookIndex;
        } else {
            myServiceMediaBinder.play(playingbookindex, mySeekBar.getProgress());

        }
        Log.wtf("msg", "Playing book: " + currentBookIndex + " Progress:" + mySeekBar.getProgress());

    }

    public void stopCurrentBook() {
        myServiceMediaBinder.stop();
        mySeekBar.setProgress(0);
        mySeekBar.setVisibility(View.INVISIBLE);
        paused = false;
    }

    public void pauseCurrentBook() {
        myServiceMediaBinder.pause();
        paused = true;
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
        getCurrentBook(i);
        detailsfrag.changeBook(mybooks.get(i));
    }

    @Override
    public void chooseBook(int id) {
        getCurrentBook(id);
        Log.wtf("msg", "Viewpager is on " + id);
    }


    public void searchDone(String searchtext) {
        Log.wtf("edittext", searchtext);
        if (searchtext != "")
            getJSON(urlSearch + searchtext);
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
