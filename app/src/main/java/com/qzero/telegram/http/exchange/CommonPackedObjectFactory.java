package com.qzero.telegram.http.exchange;

import android.content.Context;

public class CommonPackedObjectFactory implements PackedObjectFactory {

    @Override
    public PackedObject getPackedObject() {
        return new CommonPackedObject();
    }

    @Override
    public PackedObject getParameter(Context context) {
        PackedObject parameter=getPackedObject();
        return parameter;
    }

}
