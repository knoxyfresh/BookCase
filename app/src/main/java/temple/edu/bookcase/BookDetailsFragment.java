package temple.edu.bookcase;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link BookDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookDetailsFragment extends Fragment {
    // Store instance variables
    private Book mybook;

    // newInstance constructor for creating fragment with arguments
    public static BookDetailsFragment newInstance(Book book) {
        BookDetailsFragment fragmentFirst = new BookDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("book",book);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
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
        try{

        Book mybook = getArguments().getParcelable("book");
        title.setText(mybook.title);
        author.setText(mybook.author);
        id.setText(Integer.toString(mybook.id));
        published.setText(Integer.toString(mybook.published));
        Picasso.get().load(mybook.coverURL.toString()).into(img);
        }catch(Exception ex){
            Log.wtf("PROBLEM!",ex.toString());
        }
        return view;
    }
}
