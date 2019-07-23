package com.eis.ileadbyexample.Models;

public class BtnEnDsResponce {
    private boolean fc;
    private boolean lc;

    public BtnEnDsResponce(boolean fc, boolean lc) {
        this.fc = fc;
        this.lc = lc;
    }

    public boolean isFc() {
        return fc;
    }

    public void setFc(boolean fc) {
        this.fc = fc;
    }

    public boolean isLc() {
        return lc;
    }

    public void setLc(boolean lc) {
        this.lc = lc;
    }
}
