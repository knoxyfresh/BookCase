package temple.edu.bookcase;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link viewPagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class viewPagerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private ViewPager myvp;
    private ArrayList<Book> bookList;
    private chooseListener mListener;

    public viewPagerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static viewPagerFragment newInstance(ArrayList<Book> books, int position) {
        viewPagerFragment fragment = new viewPagerFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("booklist", books);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    public static viewPagerFragment newInstance(ArrayList<Book> books) {
        viewPagerFragment fragment = new viewPagerFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("booklist", books);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        ArrayList<Book> booklist = getArguments().getParcelableArrayList("booklist");
        bookList = booklist;
        ViewPager vp = view.findViewById(R.id.viewPagerInViewPagerFragment);
        MyBookAdapter adapter = new MyBookAdapter(getChildFragmentManager(), booklist);
        vp.setAdapter(adapter);
        if (getArguments().containsKey("position")) {
            int position = getArguments().getInt("position");
            vp.setCurrentItem(position);
        }
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mListener.chooseBook(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        myvp = vp;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            bookList = savedInstanceState.getParcelableArrayList("booklist");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("booklist", bookList);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof chooseListener) {
            mListener = (chooseListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public ArrayList<Book> getBooks() {
        bookList = getArguments().getParcelableArrayList("booklist");
        return bookList;
    }


    void setPosition(int position) {
        myvp.setCurrentItem(position);
    }


    public interface chooseListener {
        // TODO: Update argument type and name
        void chooseBook(int id);
    }
}
