package eu.visiton.app.ui.profile;

import eu.visiton.app.responses.MyProfileResponse;

public interface MyProfileInteractionListener {
    public void clickOnCamera();

    public void editUser(MyProfileResponse u);
}
