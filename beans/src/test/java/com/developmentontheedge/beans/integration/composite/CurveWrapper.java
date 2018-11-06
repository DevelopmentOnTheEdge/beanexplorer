package com.developmentontheedge.beans.integration.composite;

import com.developmentontheedge.beans.Option;
import com.developmentontheedge.beans.annot.PropertyName;

public class CurveWrapper extends Option {
    Curve[] curves;
    Curve singleCurve = new Curve();
    String test = "";

    public CurveWrapper()
    {
        curves = new Curve[1];
        curves[0] = new Curve();
    }

    @PropertyName("Curves list")
    public Curve[] getCurves() {
        return curves;
    }

    public void setCurves(Curve[] curves) {
        Object oldValue = this.curves;
        this.curves = curves;
        firePropertyChange("curves", oldValue, curves);
    }

    @PropertyName("Useless string")
    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        String oldValue = this.test;
        this.test = test;
        firePropertyChange("test", oldValue, test);
    }

    @PropertyName("Single curve")
    public Curve getSingleCurve() {
        return singleCurve;
    }

    public void setSingleCurve(Curve singleCurve) {
        Object oldValue = this.singleCurve;
        this.singleCurve = singleCurve;
        firePropertyChange("singleCurve", oldValue, singleCurve);
    }


}
