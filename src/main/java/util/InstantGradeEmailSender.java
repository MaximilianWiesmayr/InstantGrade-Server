package util;

import entity.User;

public class InstantGradeEmailSender implements EmailSender{

    @Override
    public void sendAuthEmail(User user) {
        try {
            EmailUtil.emailauth(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
