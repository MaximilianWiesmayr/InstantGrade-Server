package Fakes;

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
        Date date = new Date();
        return new User(
                new ObjectId(date),
                "Maximilian",
                "Wiesmayr",
                "User",
                "Passme12",
                "maximilianwiesmayr",
                new Settings(),
                10,
                AccountType.NOT_VERIFIED,
                SubscriptionStatus.BASIC,
                "token_man"
        );
    }

    public static User getFakeVerifiedUser() {
        Date date = new Date();
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

    public static User getFakeUnverifiedUserWithSameEmailButDifferentUsername() {
        Date date = new Date();
        return new User(
                new ObjectId(date),
                "Maximilian",
                "Wiesmayr",
                "User1",
                "Passme12",
                "maximilianwiesmayr@gmx.at",
                new Settings(),
                10,
                AccountType.NOT_VERIFIED,
                SubscriptionStatus.BASIC,
                "token_man"
        );
    }

    public static User getFakeUnverifiedUserWithSameUsernameButDifferentEmail() {
        Date date = new Date();
        return new User(
                new ObjectId(date),
                "Maximilian",
                "Wiesmayr",
                "User",
                "Passme12",
                "maxi.wiesmayrr@gmail.com",
                new Settings(),
                10,
                AccountType.NOT_VERIFIED,
                SubscriptionStatus.BASIC,
                "token_man"
        );
    }


}