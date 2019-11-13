package temple.edu.bookcase;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookChooserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookChooserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookChooserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "Books";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private ArrayList<Book> bookList=new ArrayList<Book>();

    private OnFragmentInteractionListener mListener;

    public BookChooserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BookChooserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookChooserFragment newInstance(ArrayList<Book> booklist) {
        BookChooserFragment fragment = new BookChooserFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("booklist",booklist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_first, container, false);
        ListView lv = v.findViewById(R.id.listViewMain);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListener.ChooseItem(i);

            }
        });
        ArrayList<Book> booklist = getArguments().getParcelableArrayList("booklist");
        bookList=booklist;
        ArrayList<String> titles = new ArrayList<String>();

        for(int i=0;i<booklist.size();i++){
            titles.add(booklist.get(i).title);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,titles);
        Log.wtf("ARRAY",titles.toString());
        lv.setAdapter(adapter);
        return v;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public ArrayList<Book> getBooks(){
        bookList = getArguments().getParcelableArrayList("booklist");
        return bookList;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void ChooseItem(int i);
    }
}
