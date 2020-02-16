package com.example.gerenciamentodesalas;

import com.airbnb.lottie.LottieComposition;

public class Lottie {
    private final LottieComposition lotComp;

    public Lottie(LottieComposition lotComp){
        this.lotComp = lotComp;
    }

    public LottieComposition getLotComp() {
         return lotComp;
    }
}
