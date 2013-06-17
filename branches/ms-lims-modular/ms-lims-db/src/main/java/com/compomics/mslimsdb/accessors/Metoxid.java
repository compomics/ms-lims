/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 6-feb-03
 * Time: 7:38:59
 */
package com.compomics.mslimsdb.accessors;

import org.apache.log4j.Logger;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:17 $
 */

/**
 * This class extends MetoxidTableAccessor and implements the Externalizable interface.
 *
 * @author Lennart Martens
 */
public class Metoxid extends MetoxidTableAccessor implements Externalizable {
    // Class specific log4j logger for Metoxid instances.
    private static Logger logger = Logger.getLogger(Metoxid.class);

    /**
     * Default constructor.
     */
    public Metoxid() {
        super();
    }

    public Metoxid(HashMap aHM) {
        super(aHM);
    }

    /**
     * The object implements the writeExternal method to save its contents by calling the methods of DataOutput for its
     * primitive values or calling the writeObject method of ObjectOutput for objects, strings, and arrays.
     *
     * @param out the stream to write the object to
     * @throws IOException Includes any I/O exceptions that may occur
     * @serialData Overriding methods should use this tag to describe the data layout of this Externalizable object.
     * List the sequence of element types and, if possible, relate the element to a public/protected field and/or method
     * of this Externalizable class.
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(iUpdated);
        out.writeLong(iId);
        out.writeObject(iFilename);
        out.writeObject(iAccession);
        out.writeLong(iStart);
        out.writeLong(iEnd);
        out.writeObject(iEnzymatic);
        out.writeObject(iSequence);
        out.writeObject(iModified_sequence);
        out.writeLong(iScore);
        out.writeObject(iCal_mass);
        out.writeObject(iExp_mass);
        out.writeInt(iValid);
    }

    /**
     * The object implements the readExternal method to restore its contents by calling the methods of DataInput for
     * primitive types and readObject for objects, strings and arrays.  The readExternal method must read the values in
     * the same sequence and with the same types as were written by writeExternal.
     *
     * @param in the stream to read data from in order to restore the object
     * @throws IOException            if I/O errors occur
     * @throws ClassNotFoundException If the class for an object being restored cannot be found.
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.iUpdated = in.readBoolean();
        this.iId = in.readInt();
        long times = in.readLong();
        this.iFilename = (String) in.readObject();
        this.iAccession = (String) in.readObject();
        this.iStart = in.readLong();
        this.iEnd = in.readLong();
        this.iEnzymatic = (String) in.readObject();
        this.iSequence = (String) in.readObject();
        this.iModified_sequence = (String) in.readObject();
        this.iScore = in.readLong();
        this.iCal_mass = (Number) in.readObject();
        this.iExp_mass = (Number) in.readObject();
        this.iValid = in.readInt();
    }
}
