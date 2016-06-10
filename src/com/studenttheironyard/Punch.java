package com.studenttheironyard;

/**
 * Created by hoseasandstrom on 6/9/16.
 */
public class Punch {
    String traditionalPunch;
    String donkeyPunch;
    String oneInchPunch;
    String throatPunch;
    String suckaPunch;
    String additionalComments;

    public Punch(String traditionalPunch, String donkeyPunch, String oneInchPunch, String throatPunch, String suckaPunch, String additionalComments) {
        this.traditionalPunch = traditionalPunch;
        this.donkeyPunch = donkeyPunch;
        this.oneInchPunch = oneInchPunch;
        this.throatPunch = throatPunch;
        this.suckaPunch = suckaPunch;
        this.additionalComments = additionalComments;
    }

    public String getTraditionalPunch() {
        return traditionalPunch;
    }

    public String getDonkeyPunch() {
        return donkeyPunch;
    }

    public String getOneInchPunch() {
        return oneInchPunch;
    }

    public String getThroatPunch() {
        return throatPunch;
    }

    public String getSuckaPunch() {
        return suckaPunch;
    }

    public String getAdditionalComments() {
        return additionalComments;
    }

    @Override
    public String toString() {
        return "Punch{" +
                "traditionalPunch='" + traditionalPunch + '\'' +
                ", donkeyPunch='" + donkeyPunch + '\'' +
                ", oneInchPunch='" + oneInchPunch + '\'' +
                ", throatPunch='" + throatPunch + '\'' +
                ", suckaPunch='" + suckaPunch + '\'' +
                ", additionalComments='" + additionalComments + '\'' +
                '}';
    }
}
