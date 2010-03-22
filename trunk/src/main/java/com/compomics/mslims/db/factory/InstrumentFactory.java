package com.compomics.mslims.db.factory;

import com.compomics.mslims.db.accessors.Instrument;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA. User: Kenny Date: 31-okt-2008 Time: 11:44:09 The 'InstrumentFactory ' class was created
 * for
 */
public class InstrumentFactory {

    public static final int WATERS_QTOF_PREMIER = 0;
    public static final int BRUKER_ESQUIRE = 1;
    public static final int BRUKER_ULTRAFLEX = 2;
    public static final int AGILENT_ESQUIRE = 3;
    public static final int ABI_4700 = 4;
    public static final int ABI_4800 = 5;
    public static final int MICROMASS_QTOF = 6;
    public static final int THERMO_FT_ICR = 7;
    public static final int THERMO_ORBITRAP = 8;
    //public static final int UNKNOWN = 9;


    private static int lInstrumentCount = 9;

    public static Instrument createInstrument(int aInstrumentType) {
        HashMap lParams = new HashMap();
        switch (aInstrumentType) {
            case BRUKER_ESQUIRE:
                lParams.put(Instrument.NAME, "Bruker Esquire HCT");
                lParams.put(Instrument.DESCRIPTION, "The Bruker Esquire HCT ESI-Iontrap mass spectrometer with an inert CapLC system");
                lParams.put(Instrument.STORAGECLASSNAME, "com.compomics.mslims.util.fileio.EsquireSpectrumStorageEngine");
                lParams.put(Instrument.PROPERTIESFILENAME, "EsquireSpectrumStorage.properties");
                lParams.put(Instrument.DIFFERENTIAL_CALIBRATION, 0.238714);
                break;

            case BRUKER_ULTRAFLEX:
                lParams.put(Instrument.NAME, "Bruker Ultraflex");
                lParams.put(Instrument.DESCRIPTION, "The Bruker Ultraflex MALDI TOF-TOF mass spectrometer");
                lParams.put(Instrument.STORAGECLASSNAME, "com.compomics.mslims.util.fileio.UltraflexSpectrumStorageEngine");
                lParams.put(Instrument.PROPERTIESFILENAME, "UltraflexSpectrumStorage.properties");
                break;


            case AGILENT_ESQUIRE:
                lParams.put(Instrument.NAME, "Agilent Esquire HCT");
                lParams.put(Instrument.DESCRIPTION, "The Agilent Esquire HCT ESI-Iontrap mass spectrometer with a chip LC system");
                lParams.put(Instrument.STORAGECLASSNAME, "com.compomics.mslims.util.fileio.EsquireSpectrumStorageEngine");
                lParams.put(Instrument.PROPERTIESFILENAME, "EsquireSpectrumStorage.properties");
                break;

            case ABI_4700:
                lParams.put(Instrument.NAME, "ABI4700");
                lParams.put(Instrument.DESCRIPTION, "Applied Biosystems 4700 Maldi-TOFTOF");
                lParams.put(Instrument.STORAGECLASSNAME, "com.compomics.mslims.util.fileio.ABI4700SpectrumStorageEngine");
                lParams.put(Instrument.PROPERTIESFILENAME, "QTOFSpectrumStorage.properties");
                break;

            case ABI_4800:
                lParams.put(Instrument.NAME, "ABI4800");
                lParams.put(Instrument.DESCRIPTION, "Applied Biosystems 4800 Maldi-TOFTOF");
                lParams.put(Instrument.STORAGECLASSNAME, "com.compomics.mslims.util.fileio.ABI4800SpectrumStorageEngine");
                lParams.put(Instrument.PROPERTIESFILENAME, "QTOFSpectrumStorage.properties");
                break;

            case MICROMASS_QTOF:
                lParams.put(Instrument.NAME, "Micromass Q-TOF");
                lParams.put(Instrument.DESCRIPTION, "The Micromass Q-TOF 1 ESI-QTOF mass spectrometer with a CapLC system");
                lParams.put(Instrument.STORAGECLASSNAME, "com.compomics.mslims.util.fileio.QTOFSpectrumStorageEngine");
                lParams.put(Instrument.PROPERTIESFILENAME, "QTOFSpectrumStorage.properties");
                lParams.put(Instrument.DIFFERENTIAL_CALIBRATION, 0.238714);
                break;

            case WATERS_QTOF_PREMIER:
                lParams.put(Instrument.NAME, "Waters Q-TOF Premier");
                lParams.put(Instrument.DESCRIPTION, "The Waters Q-TOF Premier ESI-QTOF mass spectrometer with a nano-Acquity LC system");
                lParams.put(Instrument.STORAGECLASSNAME, "com.compomics.mslims.util.fileio.QTOFSpectrumStorageEngine");
                lParams.put(Instrument.PROPERTIESFILENAME, "QTOFSpectrumStorage.properties");
                lParams.put(Instrument.DIFFERENTIAL_CALIBRATION, 0.14277715);
                break;

            case THERMO_FT_ICR:
                lParams.put(Instrument.NAME, "Thermo-Finnigan FT-ICR");
                lParams.put(Instrument.DESCRIPTION, "The Thermo-Finigan Fourier Transform mass spectrometer combined with a linear iontrap.");
                lParams.put(Instrument.STORAGECLASSNAME, null);
                lParams.put(Instrument.PROPERTIESFILENAME, null);
                lParams.put(Instrument.DIFFERENTIAL_CALIBRATION, 0.14277715);
                break;

            case THERMO_ORBITRAP:
                lParams.put(Instrument.NAME, "Thermo-Finigan LTQ-Orbitrap");
                lParams.put(Instrument.DESCRIPTION, "The Thermo-Finigan Orbitrap mass spectrometer combined with a linear iontrap");
                lParams.put(Instrument.STORAGECLASSNAME, null);
                lParams.put(Instrument.PROPERTIESFILENAME, null);
                lParams.put(Instrument.DIFFERENTIAL_CALIBRATION, 0.14277715);
                break;

            /**
             * Removed the unknown instrument type since it cannot represent any valid spectrumfile hierarchy.
             case UNKNOWN:
             lParams.put(Instrument.NAME, "Unknown instrument");
             lParams.put(Instrument.DESCRIPTION, "Spectra coming from an unknown source.");
             lParams.put(Instrument.STORAGECLASSNAME, null);
             lParams.put(Instrument.PROPERTIESFILENAME, null);
             lParams.put(Instrument.DIFFERENTIAL_CALIBRATION, 0.14277715);
             break;
             */
        }

        return new Instrument(lParams);
    }

    public static Instrument[] createAllInstruments() {
        Instrument[] lResult = new Instrument[lInstrumentCount];
        for (int i = 0; i < lResult.length; i++) {
            lResult[i] = createInstrument(i);
        }
        return lResult;
    }

}
