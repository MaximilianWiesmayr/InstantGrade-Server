package util;

import entity.Image;
import entity.Settings;
import entity.User;
import enums.AccountType;
import enums.SubscriptionStatus;
import org.bson.types.ObjectId;

import java.util.Date;

public final class Fakes {

    private Fakes() {
    }

    public static User getFakeUnverifiedUser() {
        Date date = new Date(1620217605394L);
        return new User(
                new ObjectId(date),
                "Hello",
                "There",
                "Idk",
                "safe_pass",
                "email",
                new Settings(),
                10,
                AccountType.NOT_VERIFIED,
                SubscriptionStatus.BASIC,
                "token_man"
        );
    }

    public static User getFakeVerifiedUser() {
        Date date = new Date(1620217605394L);
        return new User(
                new ObjectId(date),
                "Maximilian",
                "Wiesmayr",
                "User",
                "Passme12",
                "maximilianwiesmayr@gmx.at",
                new Settings(),
                10,
                AccountType.VERIFIED,
                SubscriptionStatus.BASIC,
                "token_man"
        );
    }

    public static Image getSimpelImage() {
        return new Image(
                "coolImage.jpeg",
                "User"
        );
    }
}