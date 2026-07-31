package net.oktawia.crazyae2addons.client.misc;

import lombok.Setter;

import appeng.client.gui.Icon;

@Setter
public class IconButton extends appeng.client.gui.widgets.IconButton {

    private Icon icon;

    public IconButton(Icon ico, OnPress prs) {
        super(prs);
        this.icon = ico;
    }

    @Override
    protected Icon getIcon() {
        return this.icon;
    }

}
