package eu.visiton.app.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import eu.visiton.app.responses.PeopleResponse;

public class PeopleViewModel extends AndroidViewModel {
    private PeopleRepository peopleRepository;
    private LiveData<List<PeopleResponse>> people;

    public PeopleViewModel(@NonNull Application application) {
        super(application);
        peopleRepository = new PeopleRepository();
    }

    public LiveData<List<PeopleResponse>> getPeople(){
        people = peopleRepository.getUsersAndFriended();
        return people;
    }
}
