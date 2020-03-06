package eu.visiton.app.ui.people;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import eu.visiton.app.R;
import eu.visiton.app.data.PeopleViewModel;
import eu.visiton.app.responses.PeopleResponse;
import eu.visiton.app.retrofit.generator.AuthType;
import eu.visiton.app.retrofit.generator.ServiceGenerator;
import eu.visiton.app.retrofit.services.UserService;
import eu.visiton.app.ui.login.LoginActivity;
import eu.visiton.app.util.UtilToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    Context ctx;
    List<PeopleResponse> users;
    UserService service;
    String jwt, idUser;
    ImageButton action;
    EditText name, id;
    ImageView picture;
    MyPeopleRecyclerViewAdapter adapter;
    FragmentManager f = getFragmentManager();
    private int mColumnCount = 1;
    private IPeopleListener mListener;

    private PeopleViewModel peopleViewModel;


    public PeopleFragment() {
    }


    public static PeopleFragment newInstance(int columnCount) {
        PeopleFragment fragment = new PeopleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        peopleViewModel = ViewModelProviders.of(getActivity())
                .get(PeopleViewModel.class);

        jwt = UtilToken.getToken(getContext());
        idUser = UtilToken.getId(getContext());
        if (jwt == null) {

            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
        }


        UserService service = ServiceGenerator.createService(UserService.class,
                jwt, AuthType.JWT);

  /*      Call<ResponseContainer<UserResponse>> callList = service.listUsersAndFriended();

        callList.enqueue(new Callback<ResponseContainer<UserResponse>>() {
            @Override
            public void onResponse(Call<ResponseContainer<UserResponse>> call, Response<ResponseContainer<UserResponse>> response) {
                if (response.isSuccessful()) {
                    Log.d("flama", "vas flama");
                } else {
                    Toast.makeText(ctx, "You have to log in!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<UserResponse>> call, Throwable t) {
//                Toast.makeText(ctx, "TokenFailure", Toast.LENGTH_LONG).show();
            }
        });

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            ctx = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(ctx, mColumnCount));
            }

            //users = new ArrayList<>();
            //UserService service = ServiceGenerator.createService(UserService.class, jwt, AuthType.JWT);

            peopleViewModel.getPeople().observe(getActivity(), peopleResponses -> {
                users = peopleResponses;

                adapter = new MyPeopleRecyclerViewAdapter(
                        getFragmentManager(),
                        ctx,
                        users,
                        mListener);

                recyclerView.setAdapter(adapter);

                adapter.setData(users);

            });
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IPeopleListener) {
            mListener = (IPeopleListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IPeopleListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
