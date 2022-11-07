package org.example;

import java.util.Objects;

public class Layout implements Comparable<Layout>{
    String drawable;
    String pkg;
    String name;
    String component;

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getDrawable() {
        return drawable;
    }

    public void setDrawable(String drawable) {
        this.drawable = drawable;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Layout)) return false;
        Layout layout = (Layout) o;
        return Objects.equals(getPkg(), layout.getPkg());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPkg());
    }


    @Override
    public int compareTo( Layout o) {
        if (o == null) {
            return -1;
        }
        return pkg.compareTo(o.pkg);
    }
}
