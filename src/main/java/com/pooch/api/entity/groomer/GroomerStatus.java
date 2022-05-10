package com.pooch.api.entity.groomer;

import java.util.Arrays;

public enum GroomerStatus {

    /**
     * Signing up<br>
     * Refer to GroomerSignUpStatus
     */
    SIGNING_UP,

    /**
     * Done signing up, now Sales team is looking at application
     */
    PENDING_APPROVAL,

    PENDING_STRIPE,

    /**
     * Ready to bring in business
     */
    ACTIVE,

    /**
     * paused business for potential vacation, etc
     */
    PAUSED,

    /**
     * potential fraud
     */
    SUSPENDED,

    /**
     * potential hacker got information of this user
     */
    INACTIVE;

    public static boolean isAllowedToLogin(GroomerStatus status) {
        return !Arrays.asList(GroomerStatus.SUSPENDED, GroomerStatus.INACTIVE).contains(status);
    }

    public String getDisAllowedToLoginReason() {
        if (this.equals(SUSPENDED)) {
            return "Your account has been suspended. Please contact our support team.";
        } else if (this.equals(INACTIVE)) {
            return "Your account has been deactivated. Please contact our support team.";
        }
        return null;
    }
}
