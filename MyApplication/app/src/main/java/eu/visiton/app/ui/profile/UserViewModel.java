package eu.visiton.app.ui.profile;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import eu.visiton.app.responses.MyProfileResponse;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<MyProfileResponse> user = new MutableLiveData<MyProfileResponse>();
    // Master > Detail (comunicación del dato)
    public void selectUser(MyProfileResponse u) {
        user.setValue(u);
    }
    public LiveData<MyProfileResponse> getSelectedUser() {
        return user;
    }
}
