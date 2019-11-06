package temple.edu.bookcase;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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

        mybook = savedInstanceState.getParcelable("book");
    }

    public void displayBook(String title){
        TextView tvLabel = getView().findViewById(R.id.tvLabel);
        tvLabel.setText(title);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel);
        tvLabel.setText(title);
        return view;
    }
}
