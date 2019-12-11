package temple.edu.bookcase;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p>
 * to handle interaction events.
 * Use the {@link BookDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookDetailsFragment extends Fragment {
    // Store instance variables
    private Book mybook;
    private mydownloadListener mListener;
    private  mydeleteListener m2Listener;
    private  myplayListener m3Listener;

    // newInstance constructor for creating fragment with arguments
    public static BookDetailsFragment newInstance(Book book) {
        BookDetailsFragment fragmentFirst = new BookDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("book", book);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof mydownloadListener && context instanceof mydeleteListener && context instanceof myplayListener) {
            mListener = (mydownloadListener) context;
            m2Listener = (mydeleteListener) context;
            m3Listener = (myplayListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement mydownloadListener");
        }
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mybook = getArguments().getParcelable("book");
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail2, container, false);
        ImageView img = (ImageView) view.findViewById(R.id.imageViewCover);
        TextView title = (TextView) view.findViewById(R.id.textViewTitle);
        TextView author = (TextView) view.findViewById(R.id.textViewAuthor);
        TextView id = (TextView) view.findViewById(R.id.textViewBookID);
        TextView published = (TextView) view.findViewById(R.id.textViewPublished);
        Button downloadButton = (Button) view.findViewById(R.id.btnDownload);
        Button playButton = (Button) view.findViewById(R.id.buttonPlay);
        File file = new File(getContext().getFilesDir(), mybook.id + "");
        if(file.exists()){
            downloadButton.setText("DELETE");
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    m2Listener.deleteID(mybook.id);
                }
            });
        }else{

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.downloadID(mybook.id);
            }
        });
        }
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m3Listener.playID(mybook.id);
            }
        });

        try {
            Book mybook = getArguments().getParcelable("book");
            title.setText(mybook.title);
            author.setText(mybook.author);
            id.setText(Integer.toString(mybook.id));
            published.setText(Integer.toString(mybook.published));
            Picasso.get().load(mybook.coverURL.toString()).into(img);
        } catch (Exception ex) {
            Log.wtf("PROBLEM!", ex.toString());
        }
        return view;
    }

    public void changeBook(Book mybook){
        View view = getView();
        ImageView img = (ImageView) view.findViewById(R.id.imageViewCover);
        TextView title = (TextView) view.findViewById(R.id.textViewTitle);
        TextView author = (TextView) view.findViewById(R.id.textViewAuthor);
        TextView id = (TextView) view.findViewById(R.id.textViewBookID);
        TextView published = (TextView) view.findViewById(R.id.textViewPublished);
        try {
            title.setText(mybook.title);
            author.setText(mybook.author);
            id.setText(Integer.toString(mybook.id));
            published.setText(Integer.toString(mybook.published));
            Picasso.get().load(mybook.coverURL.toString()).into(img);
        } catch (Exception ex) {
            Log.wtf("PROBLEM!", ex.toString());
        }
    }

    public interface mydownloadListener{
        void downloadID(int id);
    }
    public interface mydeleteListener {
        void deleteID(int id);
    }
    public interface myplayListener{
        void playID(int id);
    }
}
