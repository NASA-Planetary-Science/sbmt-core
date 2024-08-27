package edu.jhuapl.sbmt.core.config;

import java.util.LinkedHashMap;

// Names of instruments
public final class Instrument
{
    private static final LinkedHashMap<String, Instrument> InstanceMap = new LinkedHashMap<>();

    public static final Instrument MSI = new Instrument("MSI");
    public static final Instrument NLR = new Instrument("NLR");
    public static final Instrument NIS = new Instrument("NIS");
    public static final Instrument AMICA = new Instrument("AMICA");
    public static final Instrument LIDAR = new Instrument("LIDAR");
    public static final Instrument FC = new Instrument("FC");
    public static final Instrument SSI = new Instrument("SSI");
    public static final Instrument OSIRIS = new Instrument("OSIRIS");
    public static final Instrument OLA = new Instrument("OLA");
    public static final Instrument ORX_OLA_HIGH = new Instrument("HELT");
    public static final Instrument ORX_OLA_LOW = new Instrument("LELT");
    public static final Instrument MAPCAM = new Instrument("MAPCAM");
    public static final Instrument POLYCAM = new Instrument("POLYCAM");
    public static final Instrument NAVCAM = new Instrument("NAVCAM");
    public static final Instrument OTES = new Instrument("OTES");
    public static final Instrument OVIRS = new Instrument("OVIRS");
    public static final Instrument IMAGING_DATA = new Instrument("Imaging");
    public static final Instrument MVIC = new Instrument("MVIC");
    public static final Instrument LEISA = new Instrument("LEISA");
    public static final Instrument LORRI = new Instrument("LORRI");
    public static final Instrument MOLA = new Instrument("MOLA");
    public static final Instrument GENERIC = new Instrument("GENERIC");
    public static final Instrument VIS = new Instrument("VIS");
    public static final Instrument SAMCAM = new Instrument("SAMCAM");
    public static final Instrument ISS = new Instrument("ISS");
    public static final Instrument ONC = new Instrument("ONC");
    public static final Instrument TIR = new Instrument("TIR");
    public static final Instrument LASER = new Instrument("LIDAR");
    public static final Instrument DRACO = new Instrument("DRACO");
    public static final Instrument LUKE = new Instrument("LUKE");
    public static final Instrument LEIA = new Instrument("LEIA");
    public static final Instrument ITS = new Instrument("ITS");
    public static final Instrument HRI = new Instrument("HRI");
    public static final Instrument MRI = new Instrument("MRI");
    public static final Instrument LLORRI = new Instrument("LLORRI");

    final private String instrumentId;

    private Instrument(String instrumentId)
    {
        this.instrumentId = instrumentId;
        InstanceMap.put(instrumentId.toUpperCase(), this);
    }

    public String name() {
        return instrumentId;
    }

    @Override
    public String toString()
    {
        return instrumentId;
    }

    /**
     * Return the unique {@link Instrument} instance corresponding to the specified identifier string. If a corresponding instrument
     * instance already exists, it is simply returned. Otherwise, an instance with the specified identifier is created and cached
     * for future reference. In no case does this method return null.
     * <p>
     * Instrument instances are looked up in a case-insensitive way, i.e. "LLORRI" and "llorri" would be equivalent. The case of the
     * instrument name is determined when the instrument instance is instantiated. For "built-in" instruments (see public static
     * final fields above), the case of the identifier string is established prior to all calls to the {@link #valueFor(String)}
     * method. For newer instruments that are not in the above list, the case of the identifier will be established the first time
     * {@link #valueFor(String)} is called.
     * 
     * @param instrumentId
     * @return
     */
    public static Instrument valueFor(String instrumentId)
    {
        Instrument instrument = InstanceMap.get(instrumentId.toUpperCase());
        if (instrument == null)
        {
            // This will add the instrument to the map.
            instrument = new Instrument(instrumentId);
        }
        return instrument;
    }
}
