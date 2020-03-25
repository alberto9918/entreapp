package eu.visiton.app.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import eu.visiton.app.dto.UserEditDto;
import eu.visiton.app.responses.MyProfileResponse;
import eu.visiton.app.responses.PoiResponse;
import eu.visiton.app.responses.UserEditResponse;
import retrofit2.Response;

public class ProfileViewModel extends AndroidViewModel {
    public ProfileRepository profileRepository;
    public LiveData<MyProfileResponse> userProfile;
    public LiveData<UserEditResponse> editedProfile;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        profileRepository = new ProfileRepository();
        userProfile = profileRepository.getProfile();
    }

    public LiveData<UserEditResponse> updateProfile(String id, UserEditDto userEditDto){
        profileRepository.updateProfile(id, userEditDto);
        return editedProfile;
    }

}
