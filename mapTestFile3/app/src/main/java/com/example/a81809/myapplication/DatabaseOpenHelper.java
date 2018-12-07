package com.example.a81809.myapplication;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private Context mContext;
    private String  mDBName;
    private boolean mUpgrade;

    public DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
        mDBName  = name;
        mUpgrade = false;
    }

    protected void resourceCopyFromAsset() throws IOException {
        File         of;
        File         od;
        InputStream  is;
        OutputStream os;
        byte[]       buf;
        int          size;

        of  = mContext.getDatabasePath(mDBName);
        od  = new File(of.getParent());
        od.mkdirs();

//        is  = mContext.getAssets().open(mDBName);
        is =mContext.getAssets().open(mDBName);

        is =mContext.getAssets().open("database.db");
        os  = new FileOutputStream(of);
        buf = new byte[1024];
        for ( ; ; ) {
            size = is.read(buf);
            if (0 > size) break;
            os.write(buf, 0, size);
        }
        os.flush();
        os.close();
        is.close();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        try {
            SQLiteDatabase db;

            if (! mContext.getDatabasePath(mDBName).exists()) {
                resourceCopyFromAsset();
            }
            db = super.getReadableDatabase();
            if (mUpgrade) {
                close();
                resourceCopyFromAsset();
                db = super.getReadableDatabase();
            }
            return db;
        }
        catch (IOException e) {
            Log.d("debug","pppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        try {
            SQLiteDatabase db;
            if (! mContext.getDatabasePath(mDBName).exists()) {
                resourceCopyFromAsset();
            }
            db = super.getWritableDatabase();
            if (mUpgrade) {
                close();
                resourceCopyFromAsset();
                db = super.getWritableDatabase();
            }
            return db;
        }
        catch (IOException e) {
            return null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mUpgrade = false;
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mUpgrade = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mUpgrade = true;
    }
}