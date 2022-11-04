package com.mychat_sys.idmaker;

public interface WorkerIdStrategy {
    void initialize();

    long availableWorkerId();

    void release();
}
