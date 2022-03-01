package com.panda.transextends.service;

public interface TransFactory {
    TransFile create(int transType, String ext);
}
