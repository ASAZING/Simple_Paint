package apps.dev.simplepaint;

import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;


public class PathSerializable implements Parcelable {

    Path path;
    Canvas canvas;
    int paintColor;


    public PathSerializable(Path path, Canvas canvas, int paintColor) {
        this.path = path;
        this.canvas = canvas;
        this.paintColor = paintColor;
    }

    protected PathSerializable(Parcel in) {
        paintColor = in.readInt();
    }

    public static final Creator<PathSerializable> CREATOR = new Creator<PathSerializable>() {
        @Override
        public PathSerializable createFromParcel(Parcel in) {
            return new PathSerializable(in);
        }

        @Override
        public PathSerializable[] newArray(int size) {
            return new PathSerializable[size];
        }
    };

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(paintColor);
    }
}
