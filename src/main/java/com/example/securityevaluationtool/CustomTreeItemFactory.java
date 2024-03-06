package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.AttackPattern;
import javafx.scene.control.TreeItem;

/**
 * Created the class to customize tree items
 * TODO: Decide if the class is useful. Only going to need it if I'm coloring nodes (red for ones that don't have mitigations and green for ones that have mitigations)
 */
public class CustomTreeItemFactory {

    public static TreeItem<AttackPattern> createTreeItem(AttackPattern attackPattern) {
        TreeItem<AttackPattern> treeItem = new TreeItem<>(attackPattern);
        treeItem.setValue(attackPattern);
        return treeItem;
    }
}
