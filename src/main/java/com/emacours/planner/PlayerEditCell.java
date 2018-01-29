/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner;

import static com.emacours.planner.EditCell.IDENTITY_CONVERTER;
import static com.emacours.planner.PlannerControler.NOTICE_STYLE;
import static com.emacours.planner.PlannerControler.NOTICE_STYLE_LIGHT;
import com.emacours.planner.model.Player;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 *
 * @author cyann
 */
public class PlayerEditCell extends EditCell<Player, String> {

    public PlayerEditCell(StringConverter<String> converter) {
        super(converter);
    }

    public static PlayerEditCell createPlayerEditCell() {
        return new PlayerEditCell(IDENTITY_CONVERTER);
    }

    public static Callback<TableColumn<Player, String>, TableCell<Player, String>> forTableColumn() {
        return callback -> PlayerEditCell.createPlayerEditCell();
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(item);

        if (getTableRow() == null || getTableRow().getIndex() == -1 || getTableRow().getIndex() >= getTableView().getItems().size()) {
            return;
        }

        Player player = getTableView().getItems().get(getTableRow().getIndex());
        if (player.getPlaySong() > 1) {
            setStyle(NOTICE_STYLE);
        } else if (player.getPlaySong() == 1) {
            setStyle(NOTICE_STYLE_LIGHT);
        } else {
            setStyle("");
        }
    }

}
