package com.mychat_sys.idmaker;

public interface RandomCodeStrategy {
    void init();

    int prefix();

    int next();

    void release();
}
