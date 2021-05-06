package util;

import entity.User;

public interface EmailSender {
    public void sendAuthEmail(User user);
}
