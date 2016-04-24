/**
 * Taken mostly from Stack Overflow
 * http://stackoverflow.com/questions/14976196/osmdroid-and-mapquest-how-can-i-use-jpeg-tiles
 */
package com.poss.poss;

import java.io.File;
import java.io.InputStream;
import java.util.Random;

import org.osmdroid.ResourceProxy;
import org.osmdroid.ResourceProxy.string;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.BitmapTileSourceBase.LowMemoryException;
import org.osmdroid.tileprovider.tilesource.ITileSource;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class CustomMapTile implements ITileSource {

    private static int globalOrdinal = 0;

    private final int mMinimumZoomLevel;
    private final int mMaximumZoomLevel;

    private final int mOrdinal;
    protected final String mName;
    protected final String mImageFilenameEnding;
    protected final Random random = new Random();

    private final int mTileSizePixels;

    private final string mResourceId;
    private final Context context;

    public CustomMapTile(Context context) {
        this.context = context;
        mResourceId = null;
        mOrdinal = globalOrdinal++;
        mName = "MapQuest";
        mMinimumZoomLevel = 11;
        mMaximumZoomLevel = 11;
        mTileSizePixels = 256;
        mImageFilenameEnding = ".jpg";

    }

    @Override
    public String getTileRelativeFilenameString(final MapTile tile) {
        final StringBuilder sb = new StringBuilder();
        sb.append(pathBase());
        sb.append('/');
        sb.append(tile.getZoomLevel());
        sb.append('/');
        sb.append(tile.getX());
        sb.append('/');
        sb.append(tile.getY());
        sb.append(imageFilenameEnding());
        return sb.toString();
    }

    @Override
    public Drawable getDrawable(String aFilePath) throws LowMemoryException {
        try {
            // default implementation will load the file as a bitmap and create
            // a BitmapDrawable from it
            final Bitmap bitmap = BitmapFactory.decodeFile(aFilePath);
            if (bitmap != null) {
                System.out.println("bitmap wasn't null");
                return new BitmapDrawable(context.getResources(), bitmap);
            } else {
                System.out.println("couldn't load " + aFilePath);
            }
        } catch (final OutOfMemoryError e) {
            System.gc();
        }
        return null;

    }

    @Override
    public Drawable getDrawable(InputStream aFileInputStream) throws LowMemoryException {
        try {
            // default implementation will load the file as a bitmap and create
            // a BitmapDrawable from it
            final Bitmap bitmap = BitmapFactory.decodeStream(aFileInputStream);
            if (bitmap != null) {
                return new BitmapDrawable(bitmap);
            }
        } catch (final OutOfMemoryError e) {
            System.gc();
        }
        return null;

    }

    @Override
    public int ordinal() {
        return mOrdinal;
    }

    @Override
    public String name() {
        return mName;
    }

    public String pathBase() {
        return mName;
    }

    public String imageFilenameEnding() {
        return mImageFilenameEnding;
    }

    @Override
    public int getMinimumZoomLevel() {
        return mMinimumZoomLevel;
    }

    @Override
    public int getMaximumZoomLevel() {
        return mMaximumZoomLevel;
    }

    @Override
    public int getTileSizePixels() {
        return mTileSizePixels;
    }

    public String localizedName(final ResourceProxy proxy) {
        return proxy.getString(mResourceId);
    }

}