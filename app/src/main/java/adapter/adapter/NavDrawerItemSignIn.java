package adapter.adapter;


public class NavDrawerItemSignIn {
    private boolean showNotify;
    private String title;
    private int icon,iconpad;


    public NavDrawerItemSignIn(String title,int nicon,int niconpad) {
        this.title = title;
        this.icon = nicon;
        this.iconpad = niconpad;
    }

    public NavDrawerItemSignIn(boolean showNotify, String title) {
        this.showNotify = showNotify;
        this.title = title;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIconPad(int icon2) {
        this.iconpad = icon2;
    }
    public int getIconPad() {
        return iconpad;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
