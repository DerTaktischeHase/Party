package de.teampresidency.party.utils;

/**
 * User: Timo
 * Date: 05.02.2021
 * Time: 22:03
 */
public enum Rank {

    ADMINISTRATOR("teampresidency.rank.admin"),
    DEVELOPER("teampresidency.rank.developer"),
    BUILDER("teampresidency.rank.builder"),
    SUPPORTER("teampresidency.rank.supporter"),
    YOUTUBER("teampresidency.rank.youtuber"),
    PREMIUM("teampresidency.rank.premium");

    String rank;
    Rank(String s) {
        this.rank = s;
    }

    public String toString() {
        return rank;
    }
}
