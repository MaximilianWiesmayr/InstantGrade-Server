package entity;

public class Settings {
    private boolean darkmode = false;
    private boolean navBarCollapsed = false;
    private boolean myPhotosGridView = true;

    public Settings() {
    }

    public Settings(boolean darkmode, boolean navBarCollapsed, boolean myPhotosGridView) {
        this.darkmode = darkmode;
        this.navBarCollapsed = navBarCollapsed;
        this.myPhotosGridView = myPhotosGridView;
    }

    public boolean isDarkmode() {
        return darkmode;
    }

    public void setDarkmode(boolean darkmode) {
        this.darkmode = darkmode;
    }

    public boolean isNavBarCollapsed() {
        return navBarCollapsed;
    }

    public void setNavBarCollapsed(boolean navBarCollapsed) {
        this.navBarCollapsed = navBarCollapsed;
    }

    public boolean isMyPhotosGridView() {
        return myPhotosGridView;
    }

    public void setMyPhotosGridView(boolean myPhotosGridView) {
        this.myPhotosGridView = myPhotosGridView;
    }
}
