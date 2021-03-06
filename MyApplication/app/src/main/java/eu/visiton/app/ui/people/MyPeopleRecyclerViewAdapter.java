package eu.visiton.app.ui.people;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import eu.visiton.app.R;
import eu.visiton.app.responses.PeopleResponse;
import eu.visiton.app.util.Constantes;

import java.util.List;


public class MyPeopleRecyclerViewAdapter extends RecyclerView.Adapter<MyPeopleRecyclerViewAdapter.ViewHolder> {

    private List<PeopleResponse> mValues;
    private final IPeopleListener mListener;
    Context ctx;
    FragmentManager fragmentManager;

    public MyPeopleRecyclerViewAdapter(FragmentManager f, Context ctx, List<PeopleResponse> items, IPeopleListener listener) {
        mValues = items;
        mListener = listener;
        this.ctx = ctx;
        this.fragmentManager = f;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_people, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(mValues != null) {
            holder.mItem = mValues.get(position);
            String id = mValues.get(position).get_id();
            //holder.idUser.setText(mValues.get(position).getId());
            holder.name.setText(mValues.get(position).getName());
            holder.country.setText(mValues.get(position).getCity());
            Glide.with(holder.mView).load(Constantes.FILES_BASE_URL+mValues.get(position).getPicture()).into(holder.picture);
            holder.mView.setOnClickListener(v -> {

            });

            /* holder.action.setOnClickListener(v -> {
                if(holder.action.getDrawable().equals(R.drawable.ic_person_add))
                    holder.action.setImageResource(R.drawable.ic_delete);
                else
                    holder.action.setImageResource(R.drawable.ic_person_add);

            });
            */
        }

    }



    public void setData(List<PeopleResponse> peopleList){
        this.mValues = peopleList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mValues != null){
            return mValues.size();
        }else{
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView name;
        public final TextView country;
        public final ImageView picture;
        public PeopleResponse mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            name = view.findViewById(R.id.user_name);
            country = view.findViewById(R.id.country);
            picture = view.findViewById(R.id.profilePic);
        }

    }
}
