package com.example.jorge.guidin;

import android.support.v7.app.ActionBarActivity;

public abstract class PositionActivity extends ActionBarActivity {

    public abstract void refresh(double x, double y, double z, int idCuadrante);
}