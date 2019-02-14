package apps.dev.simplepaint;

public interface OnTouchScreen {

     int ON_DOWN = 0;
     int ON_UP = 1;

    void onTouch(int event);
}
