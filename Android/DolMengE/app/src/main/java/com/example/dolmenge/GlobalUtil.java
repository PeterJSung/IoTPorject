package com.example.dolmenge;

public class GlobalUtil {
    public static int RandomRange(int n1, int n2) {
        return (int) (Math.random() * (n2 - n1 + 1)) + n1;
    }
}
