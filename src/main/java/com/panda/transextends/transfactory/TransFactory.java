package com.panda.transextends.transfactory;

public interface TransFactory {
    TransFile create(int transType, String ext);
}
