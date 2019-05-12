package com.magic.magic;

import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by : mr.lu
 * Created at : 2019-05-11 at 23:44
 * Description:
 */
public class Magic {
    private static final String TAG = "Magic";
    private static final String MAGIC_SUFFIX = "_Magic";

    public static void conjure(@NonNull Object object, @NonNull String buildType) {
        try {
            Class magicClass = Class.forName(object.getClass().getCanonicalName() + MAGIC_SUFFIX);
            Constructor constructor = magicClass.getConstructor(object.getClass(), buildType.getClass());
            constructor.newInstance(object, buildType);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "conjure: ", e);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "conjure: ", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "conjure: ", e);
        } catch (InstantiationException e) {
            Log.e(TAG, "conjure: ", e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "conjure: ", e);
        }
    }
}
