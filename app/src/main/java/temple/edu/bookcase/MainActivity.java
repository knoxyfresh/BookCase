package temple.edu.bookcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity implements BookChooserFragment.OnFragmentInteractionListener, viewPagerFragment.chooseListener, BookDetailsFragment.mydownloadListener, BookDetailsFragment.mydeleteListener, BookDetailsFragment.myplayListener {

    ArrayList<Book> mybooks = new ArrayList<Book>();
    ArrayList<Book> totalbooks = new ArrayList<Book>();
    BookDetailsFragment detailsfrag;
    EditText searchbox;
    SeekBar mySeekBar;
    Book currentBook = null;
    Book playingBook = null;
    int currentposition = 0;
    boolean paused = false;
    Gson gson = new Gson();
    int playingBookProgress;
    //    SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);
    int downloadedfileid;
    boolean playingrestored=false;
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
                if (currentBook == null) currentBook = mybooks.get(0);
                buildViews();

            } catch (Exception ex) {
                Log.wtf("results", "PROBLEM OF " + ex.toString());

            }
            return false;
        }
    });

    AudiobookService.MediaControlBinder myServiceMediaBinder;


    Handler progressHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            int progress = mySeekBar.getProgress();
            Log.wtf("Progress", Integer.toString(progress));
//            mySeekBar.setMax(60);
            mySeekBar.setProgress(progress + 1);
            if (progress == playingBook.duration) {
                myServiceMediaBinder.stop();
            }
            return false;
        }
    });
    Handler downloadDoneHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            try {
                File f = (File) message.obj;
                Toast.makeText(MainActivity.this, "Download complete!!!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("tag2", e.getMessage());
            }
            //StartFragmentsHide();
            File file = new File(getFilesDir(), downloadedfileid + "");
            if (file.exists()) {
//                myServiceMediaBinder.play(file, 0);
//                currentBook=getBookByID(downloadedfileid);
                buildViews();
            }
            return false;
        }
    });


    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, AudiobookService.class);
        getApplicationContext().bindService(serviceIntent, myConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations()) {
            getApplicationContext().unbindService(myConnection);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor prefs = getPreferences(MODE_PRIVATE).edit();
        prefs.putString("Books", gson.toJson(mybooks));
        Log.wtf("DATA","Saving Book Data "+gson.toJson(mybooks));
        prefs.putInt("currentBookPosition",currentposition);
        prefs.apply();
//        if(playingBook!=null){
//            prefs.putString("PlayingBook",gson.toJson(currentBook));
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        String booksjson = prefs.getString("Books", "");
        Log.wtf("SP", "This: " + booksjson);
        if (booksjson != "") {
            Type type = new TypeToken<List<Book>>() {
            }.getType();
            mybooks = gson.fromJson(booksjson, type);
            buildViews();
        } else {
            getJSON(urlMain);
        }
        String bookstring = prefs.getString("playingBook","");
        if(!bookstring.isEmpty()){
            playingBook=gson.fromJson(bookstring, Book.class);
            if(playingBook.progress>10) playingBook.progress-=10;
            Log.wtf("data","Restoring previous played book! Progress "+playingBook.progress+"/"+playingBook.duration);
            mySeekBar.setVisibility(View.VISIBLE);
            mySeekBar.setMax(playingBook.duration);
            mySeekBar.setProgress(playingBook.progress);
            TextView np = findViewById(R.id.textViewNowPlaying);
            np.setText("Playing: " + playingBook.title);
            playingrestored=true;

        }
        int pos = prefs.getInt("currentBookPosition",-1);
        if(pos!=-1){
            currentposition=pos;
        }
//        currentBookIndex = prefs.getInt("playingindex", -1);
//        editor.putInt("playingindex", currentBookIndex);
//        editor.putString("playingtitle", currentBook.title);
//        int currentbookindex = prefs.getInt("CurrentBookIndex",-1);
//        int progress = prefs.getInt("Progress",-1);
//        String currbookjson=prefs.getString("CurrentBook","");
//        if(currbookjson!="" && progress!=-1){
//            Book abook = gson.fromJson(currbookjson,Book.class);
//            currentBook=abook;
//            currentBookIndex=currentbookindex;
//            mySeekBar.setProgress(progress);
//        Log.wtf("MSG","Found this data from perist: "+currentBook.toString()+" Progress: "+progress);
//        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelable("book", currentBook);
//        if (playingBook != null) {
//            outState.putParcelable("playingbook", playingBook);
//        }
////        outState.putInt("bookindex", currentBookIndex);
//        outState.putInt("progress", mySeekBar.getProgress());
//        outState.putBoolean("paused", paused);
        //outState.putParcelableArrayList("booklist",mybooks);
//        Log.wtf("OKUR", "SAVINGBOOKS data " + mybooks);
        //outState.putParcelable("service",myServiceMediaBinder);


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
//        if (savedInstanceState != null) {
//            //restore info about state
//            currentBook = savedInstanceState.getParcelable("book");
//            TextView np = findViewById(R.id.textViewNowPlaying);
//            playingBook = savedInstanceState.getParcelable("playingbook");
//            if (playingBook != null) {
//                np.setText("Playing: " + playingBook.title);
//                mySeekBar.setMax(playingBook.duration);
//                mySeekBar.setVisibility(View.VISIBLE);
//                mySeekBar.setProgress(savedInstanceState.getInt("progress"));
//            }
////            currentBookIndex = savedInstanceState.getInt("bookindex");
//            paused = savedInstanceState.getBoolean("paused");
//
////            mybooks = savedInstanceState.getParcelableArrayList("booklist");
//            buildViews();
//            Log.wtf("OKUR", "Here are my books: " + mybooks);
//            //checking for old frags
//        } else {
//            getJSON(urlMain);
//        }

        searchbox = findViewById(R.id.editTextMain);


        final Button searchbutton = findViewById(R.id.buttonSearchMain);
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDone(searchbox.getText().toString());
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
                    moveProgress();
                }

            }
        });


        getSupportActionBar().hide();

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

        currentBook = getBookByID(id);
    }


    public void playCurrentBook() {
            Log.wtf("DATA","Current book to play should be restored: "+currentBook.title+"-"+playingBook.title);
        if(playingrestored && currentBook.title==playingBook.title){
            File filepl = new File(getFilesDir(), playingBook.id + "");
            myServiceMediaBinder.play(filepl,playingBook.progress);
            playingrestored=false;
            return;
        }
        mySeekBar.setVisibility(View.VISIBLE);
        if (playingBook != null)
            saveProgress();

        File file = new File(getFilesDir(), currentBook.id + "");
        if (playingBook!=null && playingBook.progress != 0 && file.exists()) {
            //resume current bookgson.toJson(mybooks)
                myServiceMediaBinder.play(file, mySeekBar.getProgress());
        } else {
            //play
            playingBook = currentBook;
            mySeekBar.setMax(playingBook.duration);
            mySeekBar.setProgress(playingBook.progress);
            TextView np = findViewById(R.id.textViewNowPlaying);
            np.setText("Playing: " + playingBook.title);
//            playingBookProgress=currentBook.progress;
            if (file.exists()) {
                myServiceMediaBinder.play(file);
            } else {
                myServiceMediaBinder.play(playingBook.id);
            }
                Log.wtf("msg", "Playing book: " + playingBook.title + "ID: "+playingBook.id+" Progress:" + mySeekBar.getProgress());
//            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
//            editor.putString("PlayingBook", gson.toJson(playingBook));
//            editor.apply();
        }
    }

//    public void resumePlaying() {
//        TextView np = findViewById(R.id.textViewNowPlaying);
//        np.setText("Playing: " + currentBook.title);
//        np.setVisibility(View.VISIBLE);
//        mySeekBar.setVisibility(View.VISIBLE);
//        mySeekBar.setMax(currentBook.duration);
//        myServiceMediaBinder.play(playingBook.id, progress);
//        paused = false;
//        File file = new File(getFilesDir(), currentBook.id + "");
//        if (file.exists()) {
//            if (playingBookProgress > 0) {
//                if (playingBookProgress > 10) {
//                    playingBookProgress -= 10;
//                }
//                mySeekBar.setProgress(playingBookProgress);
//                myServiceMediaBinder.play(file, playingBookProgress);
//            } else {
//                myServiceMediaBinder.play((file));
//                mySeekBar.setProgress(0);
//            }
//        }
//    }

    public void moveProgress() {
        playCurrentBook();
        //Log.wtf("DOIT","Playing "+mySeekBar.getProgress()+" of "+currentBook.duration);
    }

    public void stopCurrentBook() {
        myServiceMediaBinder.stop();
        mySeekBar.setProgress(0);
        playingBook.progress=0;
        TextView np = findViewById(R.id.textViewNowPlaying);
        np.setText("");
        mySeekBar.setVisibility(View.INVISIBLE);
        paused = false;
    }

    public void pauseCurrentBook() {
        myServiceMediaBinder.pause();
        paused = true;
        saveProgress();
    }

    public void saveProgress(){
        playingBook.progress = mySeekBar.getProgress();
        SharedPreferences.Editor prefs = getPreferences(MODE_PRIVATE).edit();
        File filepl = new File(getFilesDir(), playingBook.id + "");
        if(playingBook!=null && filepl.exists())
            prefs.putString("playingBook", gson.toJson(playingBook));
        prefs.apply();
    }

    public Book getBookByID(int id) {
        Book currentBook;
        for (int x = 0; x < mybooks.size(); x++) {
            currentBook = mybooks.get(x);
            if (currentBook.id == id) {
                return currentBook;
            }
        }
        return null;
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
        detailsfrag = detailsfrag.newInstance(mybooks.get(i));
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.deatilsFrameLand, detailsfrag);
        transaction.commit();
        currentBook = mybooks.get(i);
        currentposition = i;
        Log.wtf("msg", "Listview is on " + i);
    }

    @Override
    public void chooseBook(int id) {
        getCurrentBook(id);
        currentposition = id;
        Log.wtf("msg", "Viewpager is on " + id);
    }


    public void searchDone(String searchtext) {
        Log.wtf("edittext", searchtext);
        if (searchtext != "")
            getJSON(urlSearch + searchtext);
        else getJSON(urlMain);
    }

    public void makeViewPager(ArrayList<Book> books) {
        viewPagerFragment vpf = viewPagerFragment.newInstance(books, currentposition);
//        viewPagerFragment vpf = viewPagerFragment.newInstance(books, 2);
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.viewpagerFramePortrait, vpf);
        transaction.commit();
    }

    public void makeListView(ArrayList<Book> books) {
        BookChooserFragment frag = BookChooserFragment.newInstance(books);
        currentBook = mybooks.get(currentposition);
        BookDetailsFragment details = BookDetailsFragment.newInstance(currentBook);
        detailsfrag = details;
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.selectFrameLand, frag);
        transaction.replace(R.id.deatilsFrameLand, details);
        transaction.commit();
    }


    @Override
    public void downloadID(int id) {

        //downloadTask.execute("https://kamorris.com/lab/audlib/download.php?id="+id, "books/book"+id+".mp3");
        final String filename = Integer.toString(id);
        final File file = new File(getFilesDir(), filename + "");
        Toast.makeText(MainActivity.this, getBookByID(id).title + " downloading...", Toast.LENGTH_LONG).show();
        downloadedfileid = id;
        new Thread() {
            @Override
            public void run() {
                URL url;
                try {
                    url = new URL("https://kamorris.com/lab/audlib/download.php?id=" + filename);
                    InputStream is = url.openStream();
                    DataInputStream dis = new DataInputStream(is);
                    byte[] buffer = new byte[1024];
                    int length;

                    FileOutputStream fos = new FileOutputStream(file);
                    while ((length = dis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    Message message = Message.obtain();
                    message.obj = file;
                    downloadDoneHandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void playID(int id) {
        getCurrentBook(id);
        playCurrentBook();
    }

    @Override
    public void deleteID(int id) {
        File file = new File(getFilesDir(), id + "");
        if (currentBook.id == id) stopCurrentBook();
        if (file.exists()) {
            file.delete();
            Toast.makeText(MainActivity.this, getBookByID(id).title + " deleted locally", Toast.LENGTH_SHORT).show();
            buildViews();
        }
    }
}

