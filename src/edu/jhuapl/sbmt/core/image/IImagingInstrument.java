package edu.jhuapl.sbmt.core.image;

import edu.jhuapl.saavtk.util.FillDetector;
import edu.jhuapl.sbmt.config.Instrument;
import edu.jhuapl.sbmt.model.image.SpectralImageMode;
import edu.jhuapl.sbmt.query.IQueryBase;

import crucible.crust.metadata.api.Metadata;

public interface IImagingInstrument
{
    Metadata store();

    ImageType getType();

    String getFlip();

    double getRotation();

    IQueryBase getSearchQuery();

    ImageSource[] getSearchImageSources();

    SpectralImageMode getSpectralMode();

    Instrument getInstrumentName();

    FillDetector<Float> getFillDetector(Image image);

    boolean isTranspose();

}
