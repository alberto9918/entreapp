package eu.visiton.app.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.airbnb.lottie.L;

import java.util.List;

import eu.visiton.app.responses.BadgeResponse;
import retrofit2.Call;

public class BadgeViewModel extends AndroidViewModel {
    private BadgeRepository badgeRepository;
    private LiveData<List<BadgeResponse>> badges;
    private LiveData<BadgeResponse> badgeDetails;

    public BadgeViewModel(@NonNull Application application) {
        super(application);
        badgeRepository = new BadgeRepository();
    }

    public LiveData<List<BadgeResponse>> getBadgesAndEarnedFiltered(){
        badges = badgeRepository.getBadgesAndEarnedFiltered();
        return badges;
    }

    public  LiveData<List<BadgeResponse>> getBadgesAndEarned(){
        badges = badgeRepository.getBadgesAndEarned();
        return badges;
    }

    public LiveData<List<BadgeResponse>> getBadgesAndEarnedSort(boolean asc){
        badges = badgeRepository.getBadgesAndEarnedSort(asc);
        return badges;
    }

    public LiveData<BadgeResponse> getBadgeDetails(String badgeId){
        badgeDetails = badgeRepository.getBadgeDetails(badgeId);
        return badgeDetails;
    }
}
