package com.compomics.mslims.gui.interfaces;


import java.awt.*;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 13-aug-2007
 * Time: 11:17:51
 */

/**
 * Interface description: ------------------ This Interface was developed to managage the table coloring. The Table
 * combines the use of 4 colors. <ol> <li>a dark and light shade for alternating rows</li> <li>a selected and
 * non-selected color folowing the selection of columns</li> </ol>
 */
public interface TableColor {

    /**
     * The Table makes use of 4 colors. dark and light shade for alternating rows. and a selected and non-selected color
     * folowing the selection of columns.
     */

    Color getSelectedLight();

    Color getSelectedDark();

    Color getNonSelectedLight();

    Color getNonSelectedDark();

    Color getHeaderLight();

    Color getHeaderDark();

}
