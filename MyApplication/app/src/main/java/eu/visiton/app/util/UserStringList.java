package eu.visiton.app.util;

import eu.visiton.app.responses.LanguageResponse;
import eu.visiton.app.responses.UserLikesResponse;
import eu.visiton.app.responses.UserResponse;

import java.util.ArrayList;
import java.util.List;

public class UserStringList {


/*    public static List<String> arrayFavs(UserResponse user) {
        List<String> favs = new ArrayList<>();

        for (PoiResponse fav : user.getFavs()) {
            favs.add(fav.getId());
        }

        return favs;
    }*/

    public static List<String> arrayFriends(UserResponse user) {
        List<String> friends = new ArrayList<>();

//        for (UserResponse friend : user.getFriends()) {
//            friends.add(friend.get_Id());
//        }

        return friends;
    }

    public static List<String> arrayVisited(UserResponse user) {
        List<String> visited = new ArrayList<>();

//        for (String visit : user.getVisited()) {
//            visited.add(visit.getId());
//        }

        return visited;
    }

    public static List<String> arrayLikes(UserResponse user) {
        List<String> likes = new ArrayList<>();

        for (UserLikesResponse like : user.getLikes()) {
            likes.add(like.getId());
        }

        return likes;
    }

    public static List<String> arrayLanguages (List<LanguageResponse> list) {
        List<String> languages = new ArrayList<>();

        for (LanguageResponse language : list) {
            languages.add(language.getName());
        }

        return languages;
    }
}
