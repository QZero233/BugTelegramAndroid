package com.qzero.telegram.http.exchange;

import android.content.Context;

public interface PackedObjectFactory {

    PackedObject getPackedObject();

    PackedObject getParameter(Context context);

}
