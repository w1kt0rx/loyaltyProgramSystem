package com.example.loyaltyprogram.repository;

public interface RankingEntry {
    Long getUserId();

    String getDisplayName();

    int getTotalPoints();
}
