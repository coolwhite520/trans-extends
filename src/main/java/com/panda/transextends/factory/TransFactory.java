package com.panda.transextends.factory;

public interface TransFactory {
    TransFile create(int transType, String ext);
}
