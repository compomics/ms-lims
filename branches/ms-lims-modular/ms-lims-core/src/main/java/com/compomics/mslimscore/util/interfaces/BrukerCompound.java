/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 4-okt-2005
 * Time: 14:55:49
 */
package com.compomics.mslimscore.util.interfaces;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2005/10/27 12:33:20 $
 */

/**
 * This interface describes the behaviour for
 *
 * @author Lennart
 * @version $Id: BrukerCompound.java,v 1.3 2005/10/27 12:33:20 lennart Exp $
 */
public interface BrukerCompound {
    double getMass();

    String getPosition();

    double getRegulation(boolean aUseArea);

    double getTotalScore();

    boolean isSingle();

    double getMZForCharge(int aCharge);

    boolean passesS2nFilter(double aS2n);
}
